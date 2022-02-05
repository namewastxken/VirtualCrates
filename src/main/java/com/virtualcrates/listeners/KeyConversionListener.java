package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.event.CrateInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class KeyConversionListener implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    public KeyConversionListener(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
    }

    @SuppressWarnings("Duplicates")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRightClickKey(CrateInteractEvent e) {

        Player p = e.getPlayer();
        Crate crate = e.getCrate();

        for(Location crateLocation : CrateAPI.getBlockCrateRegistrar().getLocations()) {
            if(crateLocation.equals(e.getLocation())) {
                return;
            }
        }

        if(!config.getBoolean("key_conversion.enabled") || config.getBoolean("key_conversion.use_command_only")) {
            return;
        }

        if(config.getBoolean("key_conversion.whitelist_enabled")) {

            ArrayList<String> whitelistedCrates = (ArrayList<String>) config.getList("key_conversion.whitelist");

            if(whitelistedCrates.contains(crate.getCrateName())) {
                if (p.getItemInHand().isSimilar(crate.getItem())) {
                    e.setCancelled(true);
                    if (!p.isSneaking()) {
                        p.sendMessage(config.getString("messages.must_be_sneaking").replaceAll("&", "§"));
                        return;
                    } else {

                        int amt = p.getItemInHand().getAmount();
                        p.setItemInHand(null);
                        plugin.getPlayerDataManager().giveCrate(p, crate, amt);
                        p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%amount%", amt + "").replaceAll("%crate%", crate.getCrateName()));
                        return;
                    }
                }
            }
        } else {
            if(p.getItemInHand().isSimilar(crate.getItem())) {
                if (p.getItemInHand().isSimilar(crate.getItem())) {
                    e.setCancelled(true);
                    if (!p.isSneaking()) {
                        p.sendMessage(config.getString("messages.must_be_sneaking").replaceAll("&", "§"));
                        return;
                    } else {

                        int amt = p.getItemInHand().getAmount();
                        p.setItemInHand(null);
                        plugin.getPlayerDataManager().giveCrate(p, crate, amt);
                        p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%amount%", amt + "").replaceAll("%crate%", crate.getCrateName()));
                        return;
                    }
                }
            }
        }



    }

}
