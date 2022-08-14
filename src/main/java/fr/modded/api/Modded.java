package fr.modded.api;

import fr.modded.api.entities.User;

public interface Modded {
    ModdedAction<User> retrieveUser(String sessionToken);

    ModdedAction<String> authenticate(String username);
}
