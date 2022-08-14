package fr.modded.api;

import fr.modded.api.entities.User;
import fr.modded.api.entities.impl.UserImpl;
import fr.modded.api.requests.Requester;
import fr.modded.api.requests.Route;
import fr.modded.api.requests.action.impl.ModdedActionImpl;
import fr.modded.api.utils.NamedThreadFactory;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ModdedImpl implements Modded {
    private final String url;
    private final Requester requester;

    private final ExecutorService actionPool;
    private final ScheduledExecutorService scheduler;

    public ModdedImpl(String url) {
        this.url = url;
        this.requester = new Requester(this);
        this.actionPool = Executors.newSingleThreadExecutor(new NamedThreadFactory("Action"));
        this.scheduler = Executors.newScheduledThreadPool(5, new NamedThreadFactory("Scheduler"));
    }

    public String getUrl() {
        return url;
    }

    public Requester getRequester() {
        return requester;
    }

    public ExecutorService getActionPool() {
        return actionPool;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    @Override
    public ModdedAction<User> retrieveUser(String sessionToken) {
        return ModdedActionImpl.onRequestExecute(this, Route.Users.GET_USER,
                new JSONObject().put("sessionId", sessionToken),
                (response, request) -> new UserImpl(sessionToken, response.getObject(), this));
    }

    @Override
    public ModdedAction<String> authenticate(String username) {
        return null;
    }
}
