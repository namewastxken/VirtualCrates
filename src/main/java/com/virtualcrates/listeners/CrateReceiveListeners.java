package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.PlayerDataManager;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.event.CrateGiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class CrateReceiveListeners implements Listener {

    private VirtualCrates plugin;
    private PlayerDataManager playerDataManager;
    private MyConfig config;
    public CrateReceiveListeners(VirtualCrates instance) {
        plugin = instance;
        playerDataManager = plugin.getPlayerDataManager();
        config = plugin.getOptions();
    }


    @SuppressWarnings("Duplicates")
    @EventHandler
    public void onCrate(CrateGiveEvent e) {
        OfflinePlayer p = e.getTarget();

        if(config.getBoolean("whitelist.enabled")) {

            ArrayList<String> whitelist = (ArrayList<String>) config.getList("whitelist.allowed_keys");

            if(whitelist.contains(e.getCrate().getCrateName())) {
                e.setCancelled(true);
                playerDataManager.giveCrate(p, e.getCrate(), e.getAmount());
                if (Bukkit.getPlayer(p.getUniqueId()) != null) {

                    String messagePrefix = CrateAPI.getInstance().getConfig().getString("prefix");

                    if(!CrateAPI.getMessage("core.player_given_crate").equalsIgnoreCase("")) {
                        Bukkit.getPlayer(p.getUniqueId()).sendMessage(ChatColor.translateAlternateColorCodes('&', CrateAPI.getMessage("core.player_given_crate").replaceAll("\\{p\\}", messagePrefix).replaceAll("\\{player\\}", p.getName()).replaceAll("\\{crate\\}", e.getCrate().getCrateName()).replaceAll("\\{number\\}", e.getAmount() + "")));
                    }
                }
            } else {
                return;
            }
        } else {
            e.setCancelled(true);
            playerDataManager.giveCrate(p, e.getCrate(), e.getAmount());
            if (Bukkit.getPlayer(p.getUniqueId()) != null) {

                String messagePrefix = CrateAPI.getInstance().getConfig().getString("prefix");


                if(!CrateAPI.getMessage("core.player_given_crate").equalsIgnoreCase("")) {
                    Bukkit.getPlayer(p.getUniqueId()).sendMessage(ChatColor.translateAlternateColorCodes('&', CrateAPI.getMessage("core.player_given_crate").replaceAll("\\{p\\}", messagePrefix).replaceAll("\\{player\\}", p.getName()).replaceAll("\\{crate\\}", e.getCrate().getCrateName()).replaceAll("\\{number\\}", e.getAmount() + "")));
                }
            }
        }
    }


}
