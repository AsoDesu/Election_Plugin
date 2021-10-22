package net.asodev.election.manager;

import net.asodev.election.Main;
import net.asodev.election.web.WS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class VotingManager {

    private Main plugin;
    List<Candidate> candidates;

    boolean isVoting = false;
    String wsMsg;

    public VotingManager(Main plugin) {
        this.plugin = plugin;

        if (this.plugin.getConfig().contains("candidates")) {
            List<UUID> uuids = new ArrayList<>();
            this.plugin.getConfig().getStringList("candidates").forEach(u -> uuids.add(UUID.fromString(u)));
            setCandidates(uuids);
        }
    }

    public void setCandidates(List<UUID> players) {
        List<String> stringUUIDs = new ArrayList<>();
        candidates = new ArrayList<>();

        for (UUID player : players) {
            candidates.add(new Candidate(player));
            stringUUIDs.add(player.toString());
        }

        plugin.getConfig().set("candidates", stringUUIDs);
        plugin.saveConfig();
    }

    public void addVote(UUID uuid) {
        Candidate candidate = getCandidate(uuid);
        candidate.addVote();
    }

    public Candidate getCandidate(UUID uuid) {
        return candidates.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public Candidate getCandidate(Integer i) {
        return candidates.get(i);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void reset() {
        getCandidates().forEach(Candidate::reset);
    }

    public List<Candidate> sortCandidates() {
        candidates.sort((a, b) -> b.getVotes() - a.getVotes());
        return candidates;
    }

    public boolean isVoting() {
        return isVoting;
    }

    public void start() {
        isVoting = true;
        plugin.getTwitchManager().votingStart();
        if (plugin.getWebManager().isEnabled()) {
            wsMsg = String.format("start:%s,%s,%s,%s", getCandidate(0).getName(), getCandidate(1).getName(), getCandidate(2).getName(), getCandidate(3).getName());
            WS.broadcast(wsMsg);
        }
    }

    public void stop() {
        isVoting = false;
        plugin.getTwitchManager().votingEnd();
        if (plugin.getWebManager().isEnabled()) {
            WS.broadcast("stop:");
        }
    }

    public String getWsMsg() {
        return wsMsg;
    }
}
