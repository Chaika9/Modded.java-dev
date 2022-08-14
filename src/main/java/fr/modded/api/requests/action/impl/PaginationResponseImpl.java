package fr.modded.api.requests.action.impl;

import fr.modded.api.ModdedImpl;
import fr.modded.api.requests.Request;
import fr.modded.api.requests.Response;
import fr.modded.api.requests.Route;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class PaginationResponseImpl<T> extends PaginationActionImpl<T> {
    protected final Function<JSONObject, T> handler;

    public PaginationResponseImpl(ModdedImpl modded, Route route, JSONObject data, Function<JSONObject, T> handler) {
        super(modded, route, data);
        this.handler = handler;
    }

    public static <T> PaginationResponseImpl<T> onPagination(ModdedImpl modded, Route route, JSONObject data, Function<JSONObject, T> handler) {
        return new PaginationResponseImpl<>(modded, route, data, handler);
    }

    public static <T> PaginationResponseImpl<T> onPagination(ModdedImpl modded, Route route, Function<JSONObject, T> handler) {
        return new PaginationResponseImpl<>(modded, route, null, handler);
    }

    @Override
    public void handleSuccess(Response response, Request<List<T>> request) {
        final List<T> entities = new LinkedList<>();
        final JSONObject object = response.getObject();

        for (final Object o : object.getJSONArray("servers")) {
            final JSONObject json = new JSONObject();
            json.put("id", o.toString());
            final T entity = handler.apply(json);
            entities.add(entity);
            if (useCache) {
                cached.add(entity);
            }
        }
        currentIndex = getCurrentIndex() + entities.size();
        request.onSuccess(entities);
    }
}
