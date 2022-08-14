package fr.modded.api.utils;

import java.util.concurrent.ThreadFactory;

public final class NamedThreadFactory implements ThreadFactory {

    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r, String.format("Modded-%s-Worker", name));
        thread.setDaemon(true);
        return thread;
    }
}
