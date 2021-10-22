package net.asodev.election.events;

import net.asodev.election.Main;
import net.asodev.election.manager.Candidate;
import net.asodev.election.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

import static net.asodev.election.utils.Utils.votingPaperName;

public class Events implements Listener {

    Main plugin;
    String invName = Utils.t("&a&lSelect your vote.");

    public Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void Interact(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if ((e.getItem().getType() == Material.PAPER) && (e.getItem().getItemMeta().getDisplayName().equals(votingPaperName))) {
            Inventory inv = Bukkit.createInventory(null, 9, invName);

            int pos = 0;
            List<Candidate> c = plugin.getVotingManager().getCandidates();
            for (int i = 0; i < 9; i++) {
                if (i % 2 != 0) {
                    inv.setItem(i, Utils.createVotingButton(c.get(pos).getUuid()));
                    pos++;
                }
            }

            e.getPlayer().openInventory(inv);
        }
    }

    @EventHandler
    public void Click(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(invName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();

            int slot = e.getWhoClicked().getInventory().getHeldItemSlot();
            ItemStack item = Utils.votedPaper(1, meta.getOwningPlayer().getUniqueId());

            e.getWhoClicked().getInventory().setItem(slot, item);
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void Hopper(InventoryPickupItemEvent e) {
        if (e.getInventory().getHolder() instanceof Hopper) {
            Hopper hopper = (Hopper) e.getInventory().getHolder();

            if (hopper.getBlock().getLocation().equals(plugin.getConfig().getLocation("hopper"))) {
                if (e.getItem().getItemStack().getItemMeta().getDisplayName().equals(Utils.votedPaperName)) {
                    UUID uuid = UUID.fromString(e.getItem().getItemStack().getItemMeta().getLore().get(0));

                    plugin.getVotingManager().addVote(uuid);

                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            e.getItem().remove();
                            e.getInventory().clear();
                        }
                    }.runTaskLater(plugin, 1);
                }
            }
        }
    }

}
