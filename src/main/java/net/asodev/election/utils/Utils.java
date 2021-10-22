package net.asodev.election.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static String votingPaperName = Utils.t("&a&nVoting Slip");
    public static String votedPaperName = Utils.t("&b&nCompleted Voting Slip");

    public static String t(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static ItemStack votingPaper(int amount) {
        ItemStack items = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = items.getItemMeta();

        meta.setDisplayName(votingPaperName);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.CHANNELING, 1, true);

        items.setItemMeta(meta);

        return items;
    }

    public static ItemStack votedPaper(int amount, UUID voted) {
        ItemStack items = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = items.getItemMeta();

        meta.setDisplayName(votedPaperName);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.CHANNELING, 1, true);

        List<String> lore = new ArrayList<>();
        lore.add(voted.toString());

        meta.setLore(lore);

        items.setItemMeta(meta);

        return items;
    }

    public static ItemStack createVotingButton(UUID owner) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(Utils.t("&a&nVote for " + player.getName()));
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
