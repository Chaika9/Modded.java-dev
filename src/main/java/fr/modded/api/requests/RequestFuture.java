package fr.modded.api.requests;

import fr.modded.api.ModdedImpl;
import fr.modded.api.requests.action.impl.ModdedActionImpl;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class RequestFuture<T> extends CompletableFuture<T> {
    public RequestFuture(ModdedImpl modded, ModdedActionImpl<T> action, Route route, JSONObject requestBody) {
        final Request<T> request = new Request<>(action, route, requestBody, this::complete, this::completeExceptionally);
        modded.getRequester().request(request);
    }
}
