package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PreviewCloseListener implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    public PreviewCloseListener(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
    }

    private ArrayList<Crate> getCrates() {
        ArrayList<Crate> crates = new ArrayList<Crate>();

        crates.clear();

        for(String item : config.getConfigurationSection("items").getKeys(false)) {

            Crate crate = CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + item + ".crate_name"));

            if(crate == null) {
                System.out.println("[VirtualCrates] Could not find the crate reloaded crate. Skipping...");
                continue;
            }

            crates.add(CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + item + ".crate_name")));
        }

        return crates;
    }




    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();

        for(Crate crate : getCrates()) {



            if (e.getInventory().getTitle().equalsIgnoreCase(crate.getDisplayName().replaceAll("&", "§"))) {
                for(Location crates : CrateAPI.getBlockCrateRegistrar().getLocations()) {

                    if(crates.getWorld() != p.getWorld()) {
                        continue;
                    }

                    if(crates.distance(p.getLocation()) <= 5) {
                        return;
                    }
                }

                new BukkitRunnable() {
                    public void run() {
                        if (p.getOpenInventory().getType() == InventoryType.CRAFTING || p.getOpenInventory().getType() == InventoryType.CREATIVE) {
                            p.openInventory(plugin.getCrateInventoryManager().getVirtualCrates(p));
                        }
                    }
                }.runTaskLater(plugin, 11);
                return;
            }
        }
    }


}
