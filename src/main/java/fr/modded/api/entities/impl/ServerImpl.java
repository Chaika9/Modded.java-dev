package fr.modded.api.entities.impl;

import fr.modded.api.ModdedAction;
import fr.modded.api.ModdedImpl;
import fr.modded.api.entities.Server;
import fr.modded.api.entities.ServerAccessType;
import fr.modded.api.entities.ServerStatus;
import org.json.JSONObject;

public class ServerImpl implements Server {
    private final ModdedImpl modded;

    private String id;
    private String name;
    private String description;

    private ServerAccessType accessType;
    private ServerStatus status;

    public ServerImpl(JSONObject json, ModdedImpl modded) {
        this.modded = modded;
        unserialize(json);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ServerAccessType getAccessType() {
        return accessType;
    }

    @Override
    public ServerStatus getStatus() {
        return status;
    }

    @Override
    public ModdedAction<Void> start() {
        //return ServerActionImpl.onRequestExecute(modded, Route.Servers.START_SERVER, id);
        return null;
    }

    @Override
    public ModdedAction<Void> stop() {
        // return ServerActionImpl.onRequestExecute(modded, Route.Servers.STOP_SERVER, id);
        return null;
    }

    @Override
    public ModdedAction<Void> reset() {
        // return ServerActionImpl.onRequestExecute(modded, Route.Servers.RESET_SERVER, id);
        return null;
    }

    private void unserialize(JSONObject json) {
        this.id = json.getString("id");
        this.name = json.getString("name");
        this.description = json.getString("description");

        this.accessType = ServerAccessType.valueOf(json.getString("accessType"));
        this.status = ServerStatus.valueOf(json.getString("status"));
    }
}
