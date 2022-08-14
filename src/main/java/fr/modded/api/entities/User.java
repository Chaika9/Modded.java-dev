package fr.modded.api.entities;

import fr.modded.api.ModdedAction;
import fr.modded.api.PaginationAction;

import java.util.List;

public interface User {
    ModdedAction<Server> retrieveServerById(String id);

    ModdedAction<List<Server>> retrieveServers();

    PaginationAction<Server> retrieveFriendsServers();

    PaginationAction<Server> retrievePublicServers();

    PaginationAction<Server> retrieveOfficialServers();
}
