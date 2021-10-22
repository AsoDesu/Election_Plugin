package net.asodev.election.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Candidate {
    private UUID uuid;
    private Integer votes;
    private String name;

    public Candidate(UUID u) {
        uuid = u;
        votes = 0;
        this.name = Bukkit.getOfflinePlayer(u).getName();
    }

    public Integer getVotes() {
        return votes;
    }

    public Integer addVote() {
        votes++;
        return votes;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        votes = 0;
    }
}
