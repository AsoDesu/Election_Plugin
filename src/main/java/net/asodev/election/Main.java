package net.asodev.election;

import net.asodev.election.commands.Election;
import net.asodev.election.events.Events;
import net.asodev.election.manager.TwitchManager;
import net.asodev.election.manager.VotingManager;
import net.asodev.election.manager.WebManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private VotingManager votingManager;
    private TwitchManager twitchManager;
    private WebManager webManager;

    @Override
    public void onEnable() {
        votingManager = new VotingManager(this);
        twitchManager = new TwitchManager(this);
        webManager = new WebManager(this);

        getServer().getPluginManager().registerEvents(new Events(this), this);

        // Commands
        new Election(this);

        MainInstance.setPlugin(this);
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        twitchManager.disconnect();
    }

    public VotingManager getVotingManager() {
        return votingManager;
    }
    public TwitchManager getTwitchManager() {
        return twitchManager;
    }
    public WebManager getWebManager() {
        return webManager;
    }
}
