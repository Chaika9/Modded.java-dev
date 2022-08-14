package fr.modded.api.requests;

public class Route {
    public static class Users {
        public static final Route GET_USER = new Route("user/profile");
    }

    public static class Servers {
        public static final Route GET_SERVER = new Route("user/server/get");

        public static final Route LIST_SERVERS = new Route("user/server/list");
        public static final Route LIST_FRIEND_SERVERS = new Route("user/server/list/friend");
        public static final Route LIST_PUBLIC_SERVERS = new Route("user/server/list/public");
        public static final Route LIST_OFFICIAL_SERVERS = new Route("user/server/list/official");

        public static final Route START_SERVER = new Route("user/server/controller/start");
        public static final Route STOP_SERVER = new Route("user/server/controller/stop");
        public static final Route RESET_SERVER = new Route("user/server/controller/reset");
    }

    private final String route;

    public Route(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
