package net.asodev.election.commands;

import net.asodev.election.Main;
import net.asodev.election.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Election implements CommandExecutor {

    private Main plugin;

    public Election(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("election").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.t("&cOnly players can execute this command."));
            return true;
        }
        Player p = (Player) s;

        switch (args[0]) {
            case "players":
                if (args.length != 5) {
                    s.sendMessage(Utils.t("&cYou must specify the 4 players that are running."));
                    return true;
                }

                List<UUID> players = new ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    Player tp = Bukkit.getPlayer(args[i]);
                    if (tp == null) {
                        s.sendMessage(Utils.t("&cPlayer &7" + args[i] + "&c was not found."));
                        return true;
                    }
                    players.add(tp.getUniqueId());
                }
                plugin.getVotingManager().setCandidates(players);

                s.sendMessage(Utils.t("&aSet the candidates."));
                return true;
            case "hopper":
                Block tb = p.getTargetBlock(null, 10);

                if (tb.getType() != Material.HOPPER) {
                    s.sendMessage(Utils.t("&cThe block you're looking at is not a hopper."));
                    return true;
                }

                plugin.getConfig().set("hopper", tb.getLocation());
                plugin.saveConfig();

                s.sendMessage(Utils.t("&aSet the voting hopper."));
                return true;
            case "start":
                if (p.getInventory().getItemInMainHand().getType() != Material.PAPER) {
                    s.sendMessage(Utils.t("&cYou must be holding paper to start the voting."));
                    return true;
                }

                plugin.getVotingManager().reset();
                plugin.getVotingManager().start();
                int amount = p.getInventory().getItemInMainHand().getAmount();

                p.getInventory().setItemInMainHand(Utils.votingPaper(amount));

                s.sendMessage(Utils.t("&aStarted Voting."));
                return true;
            case "results":
                p.sendMessage(Utils.t("&a&lResults: "));
                plugin.getVotingManager().sortCandidates().forEach(c -> {
                    p.sendMessage(Utils.t(String.format(" &a%d - &7%s", c.getVotes(), c.getName())));
                });
                plugin.getVotingManager().stop();
                return true;
            case "join":
                plugin.getTwitchManager().joinChannel(args[1]);
                s.sendMessage(Utils.t("&aJoined twitch channel, &7" + args[1]));
                return true;
        }

        return true;
    }
}
