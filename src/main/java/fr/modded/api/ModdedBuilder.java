package fr.modded.api;

public class ModdedBuilder {
    public static Modded create(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return new ModdedImpl(url);
    }
}
