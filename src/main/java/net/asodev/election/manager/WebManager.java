package net.asodev.election.manager;

import net.asodev.election.Main;
import net.asodev.election.web.WS;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static spark.Spark.*;

public class WebManager {

    Main plugin;
    Boolean enabled;

    public WebManager(Main plugin) {
        if (!plugin.getConfig().contains("port")) {
            Bukkit.getLogger().warning("Web Integration disabled, no port specified.");
            enabled = false;
            return;
        }
        enabled = true;

        port(plugin.getConfig().getInt("port"));
        webSocket("/ws", WS.class);
        init();

        new BukkitRunnable(){
            @Override
            public void run() {
                WS.broadcast("ping:");
            }
        }.runTaskTimer(plugin, 0, 100);
    }

    public Boolean isEnabled() {
        return enabled;
    }
}
