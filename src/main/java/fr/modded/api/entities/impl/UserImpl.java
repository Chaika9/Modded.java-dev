package fr.modded.api.entities.impl;

import fr.modded.api.ModdedAction;
import fr.modded.api.ModdedImpl;
import fr.modded.api.PaginationAction;
import fr.modded.api.entities.Server;
import fr.modded.api.entities.User;
import fr.modded.api.requests.Route;
import fr.modded.api.requests.action.impl.ModdedActionImpl;
import fr.modded.api.requests.action.impl.PaginationResponseImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserImpl implements User {
    private final ModdedImpl modded;

    private final String sessionToken;

    public UserImpl(String sessionToken, JSONObject json, ModdedImpl modded) {
        this.sessionToken = sessionToken;
        this.modded = modded;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public ModdedAction<Server> retrieveServerById(String serverId) {
        return ModdedActionImpl.onRequestExecute(modded, Route.Servers.GET_SERVER,
                new JSONObject()
                        .put("sessionId", sessionToken)
                        .put("serverId", serverId),
                (response, request) -> new ServerImpl(response.getObject(), modded, this));
    }

    @Override
    public ModdedAction<List<Server>> retrieveServers() {
        return ModdedActionImpl.onRequestExecute(modded, Route.Servers.LIST_SERVERS,
                new JSONObject().put("sessionId", sessionToken),
                (response, request) -> {
                    final List<Server> servers = new ArrayList<>();
                    final JSONArray array = response.getArray();
                    for (final Object o : array) {
                        final Server server = retrieveServerById(o.toString()).execute();
                        servers.add(server);
                    }
                    return Collections.unmodifiableList(servers);
                });
    }

    @Override
    public PaginationAction<Server> retrieveFriendsServers() {
        return PaginationResponseImpl.onPagination(modded, Route.Servers.LIST_FRIEND_SERVERS,
                new JSONObject().put("sessionId", sessionToken),
                (object) -> retrieveServerById(object.get("id").toString()).execute());
    }

    @Override
    public PaginationAction<Server> retrievePublicServers() {
        return PaginationResponseImpl.onPagination(modded, Route.Servers.LIST_PUBLIC_SERVERS,
                new JSONObject().put("sessionId", sessionToken),
                (object) -> retrieveServerById(object.get("id").toString()).execute());
    }

    @Override
    public PaginationAction<Server> retrieveOfficialServers() {
        return PaginationResponseImpl.onPagination(modded, Route.Servers.LIST_OFFICIAL_SERVERS,
                new JSONObject().put("sessionId", sessionToken),
                (object) -> retrieveServerById(object.get("id").toString()).execute());
    }
}
