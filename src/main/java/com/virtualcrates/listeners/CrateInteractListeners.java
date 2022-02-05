package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.CrateInventoryManager;
import com.virtualcrates.managers.CrateLocationManager;
import com.virtualcrates.managers.PlayerDataManager;
import com.virtualcrates.util.ItemsUtil;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrateInteractListeners implements Listener {


    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;
    private ItemsUtil itemUtil;
    private CrateLocationManager crateLocationManager;
    private CrateInventoryManager crateInventoryManager;
    public CrateInteractListeners(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        playerDataManager = plugin.getPlayerDataManager();
        itemUtil = plugin.getItemUtil();
        crateLocationManager = plugin.getCrateLocationManager();
        crateInventoryManager = plugin.getCrateInventoryManager();
    }



    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        Block block = e.getBlock();

        if(crateLocationManager.isCrateLocation(block.getLocation())) {

            e.setCancelled(true);

            if(p.hasPermission("virtualcrates.admin")) {
                if(!p.isSneaking()) {
                    p.sendMessage("§cTo remove a virtual crate, you must be sneaking when you break it.");
                    return;
                } else {
                    e.setCancelled(false);
                    plugin.getHologramManager().removeHologram(block.getLocation());
                    crateLocationManager.deleteCrateLocation(block.getLocation());
                    return;
                }
            }

        }

    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = e.getClickedBlock();

        if(crateLocationManager.isCrateLocation(block.getLocation())) {
            e.setCancelled(true);
            p.openInventory(crateInventoryManager.getVirtualCrates(p));
            p.playSound(p.getLocation(), Sound.valueOf(config.getString("sounds.open_gui.sound_name")), config.getInt("sounds.open_gui.volume"), config.getInt("sounds.open_gui.pitch"));
            return;
        }
    }

}
