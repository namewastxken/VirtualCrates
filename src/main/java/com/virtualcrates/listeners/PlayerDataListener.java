package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.managers.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerDataListener implements Listener {

    private VirtualCrates plugin;
    private PlayerDataManager playerDataManager;
    public PlayerDataListener(VirtualCrates instance) {
        this.plugin = instance;
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(playerDataManager.getPlayerDataFile(e.getPlayer()) == null) {
            playerDataManager.createPlayerDataFile(e.getPlayer());
        }
    }

}
