package fr.modded.api.test;

import fr.modded.api.Modded;
import fr.modded.api.ModdedBuilder;
import fr.modded.api.PaginationAction;
import fr.modded.api.entities.Server;
import fr.modded.api.entities.User;
import fr.modded.api.exceptions.InvalidSessionException;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final Modded modded = ModdedBuilder.create("https://api.modded.cloud");

        final User user;
        try {
            user = modded.retrieveUser("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJQYWxhZGl1bSAtIE1vZGRlZCIsImV4cCI6MTY2MDU3MDc3NiwidXVpZCI6ImJiNTc3Yjk5LTQ3NGEtNDNkZi1iYzI4LWFhYzc5ZDhiNzU3ZiJ9.AwZIT65We8ZW6cJca4BBFMuAoCjxuOgubM_TASiNFAU")
                    .execute();
        } catch (InvalidSessionException exception) {
            System.err.println("Invalid session");
            return;
        }

        final PaginationAction<Server> serverPaginationAction = user.retrieveOfficialServers();
        serverPaginationAction.limit(50);
        serverPaginationAction.skipTo(0);

        final List<Server> servers = serverPaginationAction.execute();
        for (final Server server : servers) {
            System.out.println("Name: " + server.getName());
            System.out.println("Description: " + server.getDescription());
        }
    }
}
