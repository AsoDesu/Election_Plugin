package net.asodev.election.manager;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.asodev.election.Main;
import net.asodev.election.utils.Utils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TwitchManager {

    Main plugin;
    TwitchChat client;

    List<String> votedUsers = new ArrayList<>();

    public TwitchManager(Main plugin) {
        this.plugin = plugin;

        if (!plugin.getConfig().contains("token")) {
            Bukkit.getLogger().warning("Twitch Integration disabled, no access token.");
            return;
        } else if (!plugin.getConfig().contains("channels")) {
            Bukkit.getLogger().warning("Twitch Integration disabled, no channels.");
            return;
        }

        OAuth2Credential credential = new OAuth2Credential("twitch", plugin.getConfig().getString("token"));

        client = TwitchChatBuilder.builder()
                .withChatAccount(credential)
                .build();

        plugin.getConfig().getStringList("channels").forEach(client::joinChannel);

        client.getEventManager().onEvent(ChannelMessageEvent.class, this::MessageRecived);
    }

    private void MessageRecived(ChannelMessageEvent e) {
        if (!plugin.getVotingManager().isVoting()) return;
        if (!Utils.isNumeric(e.getMessage())) return;
        int i = Integer.parseInt(e.getMessage());

        if (i > 4 || i < 1) {
            reply(e, "You need to pick a number between 1-4");
            return;
        }
        if (votedUsers.contains(e.getUser().getId())) {
            reply(e, "You've already voted.");
            return;
        }

        Candidate candidate = plugin.getVotingManager().getCandidate(i-1);
        candidate.addVote();
        votedUsers.add(e.getUser().getId());
        reply(e, "You voted for " + candidate.getName());
        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl.hasPermission("election.admin"))
                pl.sendMessage(Utils.t("[&dTwitch&r] &7" + e.getUser().getName() + "&a voted for &7" + candidate.getName()));
        });
    }

    private void reply(ChannelMessageEvent e, String msg) {
        client.sendMessage(e.getChannel().getName(), msg, null, e.getMessageEvent().getMessageId().get());
    }

    public void votingStart() {
        client.getChannels().forEach(c -> client.sendMessage(c, "Voting has started!!"));
        votedUsers.clear();
    }

    public void votingEnd() {
        votedUsers.clear();
    }

    public void joinChannel(String c) {
        List<String> channels = new ArrayList<>();
        if (plugin.getConfig().contains("channels")) channels = plugin.getConfig().getStringList("channels");
        if (channels.contains(c)) return;
        channels.add(c);
        if (client != null) client.joinChannel(c);

        plugin.getConfig().set("channels", channels);
    }

    public void disconnect() {
        client.disconnect();
    }

}
