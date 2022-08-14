package fr.modded.api.entities;

import fr.modded.api.ModdedAction;

public interface Server {
    String getId();

    String getName();

    String getDescription();

    ServerAccessType getAccessType();

    default boolean isOfficial() {
        final ServerAccessType type = getAccessType();
        return type == ServerAccessType.OFFICIAL;
    }

    default boolean isPublic() {
        final ServerAccessType type = getAccessType();
        return isOfficial() || type == ServerAccessType.PUBLIC;
    }

    ServerStatus getStatus();

    default boolean isStarted() {
        final ServerStatus type = getStatus();
        return type == ServerStatus.STARTING || type == ServerStatus.OPEN;
    }

    ModdedAction<Void> start();

    ModdedAction<Void> stop();

    ModdedAction<Void> reset();
}
