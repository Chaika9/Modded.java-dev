package fr.modded.api.requests;

import fr.modded.api.exceptions.InvalidSessionException;
import fr.modded.api.exceptions.ModdedException;
import fr.modded.api.exceptions.NotFoundException;
import fr.modded.api.requests.action.impl.ModdedActionImpl;
import org.json.JSONObject;

import java.util.function.Consumer;

public class Request<T> {
    private final ModdedActionImpl<T> action;
    private final Route route;
    private final JSONObject requestBody;
    private final Consumer<? super T> onSuccess;
    private final Consumer<? super Throwable> onFailure;

    private boolean done = false;

    public Request(ModdedActionImpl<T> action, Route route, JSONObject requestBody,
                   Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        this.action = action;
        this.route = route;
        this.requestBody = requestBody;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public void onSuccess(T success) {
        if (done) {
            return;
        }
        done = true;

        try {
            onSuccess.accept(success);
        } catch (Throwable t) {
            System.err.printf("Encountered error while processing success consumer: %s%n", t);
            throw t;
        }
    }

    public void onFailure(Throwable exception) {
        if (done) {
            return;
        }
        done = true;

        try {
            onFailure.accept(exception);
        } catch (Throwable t) {
            System.err.printf("Encountered error while processing failure consumer: %s%n", t);
            throw t;
        }
    }

    public void setOnFailure(Response response) {
        switch (response.getCode()) {
            case 500:
                onFailure(new ModdedException("Internal server error"));
                break;
            case 403:
                onFailure(new InvalidSessionException("Invalid session"));
                break;
            case 404:
                onFailure(new NotFoundException("The requested entity was not found."));
                break;
            default:
                onFailure(new ModdedException("Unknown error"));
                break;
        }
    }

    public void handleResponse(Response response) {
        action.handleResponse(response, this);
    }

    public Route getRoute() {
        return route;
    }

    public JSONObject getRequestBody() {
        return requestBody;
    }
}
