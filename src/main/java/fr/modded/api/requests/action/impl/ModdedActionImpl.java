package fr.modded.api.requests.action.impl;

import fr.modded.api.ModdedAction;
import fr.modded.api.ModdedImpl;
import fr.modded.api.exceptions.ModdedException;
import fr.modded.api.requests.Request;
import fr.modded.api.requests.RequestFuture;
import fr.modded.api.requests.Response;
import fr.modded.api.requests.Route;
import org.json.JSONObject;

import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ModdedActionImpl<T> implements ModdedAction<T> {
    private final ModdedImpl modded;
    private final Route route;
    protected final JSONObject data;
    private final BiFunction<Response, Request<T>, T> handler;

    public static <T> ModdedActionImpl<T> onRequestExecute(ModdedImpl modded, Route route, JSONObject data, BiFunction<Response, Request<T>, T> handler) {
        return new ModdedActionImpl<>(modded, route, data, handler);
    }

    public static <T> ModdedActionImpl<T> onRequestExecute(ModdedImpl modded, Route route, BiFunction<Response, Request<T>, T> handler) {
        return new ModdedActionImpl<>(modded, route, handler);
    }

    public static <T> ModdedActionImpl<T> onRequestExecute(ModdedImpl modded, Route route, JSONObject data) {
        return new ModdedActionImpl<>(modded, route, data);
    }

    public ModdedActionImpl(ModdedImpl modded, Route route) {
        this(modded, route, null, null);
    }

    public ModdedActionImpl(ModdedImpl modded, Route route, JSONObject data) {
        this(modded, route, data, null);
    }

    public ModdedActionImpl(ModdedImpl modded, Route route, BiFunction<Response, Request<T>, T> handler) {
        this(modded, route, null, handler);
    }

    public ModdedActionImpl(ModdedImpl modded, Route route, JSONObject data, BiFunction<Response, Request<T>, T> handler) {
        this.modded = modded;
        this.route = route;
        this.data = data;
        this.handler = handler;
    }

    @Override
    public T execute() {
        final JSONObject data = finalizeData();
        try {
            return new RequestFuture<>(modded, this, route, data).join();
        } catch (CompletionException exception) {
            if (exception.getCause() != null) {
                final Throwable cause = exception.getCause();
                if (cause instanceof ModdedException) {
                    throw (ModdedException) cause.fillInStackTrace();
                }
            }
            throw exception;
        }
    }

    @Override
    public void executeAsync(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        if (onSuccess == null) {
            onSuccess = DEFAULT_SUCCESS;
        }
        if (onFailure == null) {
            onFailure = DEFAULT_FAILURE;
        }

        final Consumer<? super T> finalizedSuccess = onSuccess;
        final Consumer<? super Throwable> finalizedFailure = onFailure;
        modded.getActionPool().submit(() -> {
            final JSONObject data = finalizeData();
            modded.getRequester().request(new Request<>(this, route, data, finalizedSuccess, finalizedFailure));
        });
    }

    @Override
    public ModdedImpl getModded() {
        return modded;
    }

    public void handleResponse(Response response, Request<T> request) {
        if (response.isOk()) {
            handleSuccess(response, request);
        } else {
            request.setOnFailure(response);
        }
    }

    public void handleSuccess(Response response, Request<T> request) {
        if (response.isEmpty() || handler == null) {
            request.onSuccess(null);
        } else {
            request.onSuccess(handler.apply(response, request));
        }
    }

    protected JSONObject finalizeData() {
        return data;
    }
}
