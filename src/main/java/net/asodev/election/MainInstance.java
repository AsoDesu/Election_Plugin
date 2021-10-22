package net.asodev.election;

public class MainInstance {
    private static Main plugin;

    public static void setPlugin(Main main) {
        plugin = main;
    }

    public static Main getPlugin() {
        return plugin;
    }
}
