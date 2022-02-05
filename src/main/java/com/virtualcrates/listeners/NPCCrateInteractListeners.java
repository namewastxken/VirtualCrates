package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.CrateInventoryManager;
import com.virtualcrates.managers.CrateLocationManager;
import com.virtualcrates.managers.HologramLocationManager;
import com.virtualcrates.managers.HologramManager;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCCrateInteractListeners implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    private MyConfig crateLocations;
    private CrateLocationManager locationManager;
    private CrateInventoryManager inventoryManager;
    private HologramManager hologramManager;
    private HologramLocationManager hologramLocationManager;
    public NPCCrateInteractListeners(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.crateLocations = plugin.getCratelocations();
        this.locationManager = plugin.getCrateLocationManager();
        this.inventoryManager = plugin.getCrateInventoryManager();
        this.hologramManager = plugin.getHologramManager();
        this.hologramLocationManager = plugin.getHologramLocationManager();
    }

    @EventHandler
    public void onNPCRemove(NPCRemoveEvent e) {
        NPC npc = e.getNPC();
        if(locationManager.isNPCLocation(npc)) {
            locationManager.deleteNPCLocation(npc);
            hologramManager.removeHologram(npc.getStoredLocation());
            return;
        }
    }

    @EventHandler
    public void onNPCInteract(NPCRightClickEvent e) {
        Player p = e.getClicker();
        NPC npc = e.getNPC();

        if(!locationManager.isSelectingNPC(p)) {
            if(locationManager.isNPCLocation(npc)) {
                e.setCancelled(true);
                p.openInventory(inventoryManager.getVirtualCrates(p));
                p.playSound(p.getLocation(), Sound.valueOf(config.getString("sounds.open_gui.sound_name")), config.getInt("sounds.open_gui.volume"), config.getInt("sounds.open_gui.pitch"));
                return;
            }
        } else {

            if (locationManager.isNPCLocation(npc)) {
                p.sendMessage("§cYou cannot this npc as a crate, as it is already one.");
                locationManager.stopNPCSelection(p);
                return;
            }
            locationManager.stopNPCSelection(p);
            locationManager.setNPCLocation(npc);
            p.sendMessage(config.getString("messages.set_npc_as_virtualcrate").replaceAll("&", "§"));

            if (config.getBoolean("holograms.enabled")) {
                plugin.getHologramManager().addNPCHologram(npc.getStoredLocation());
            }
            return;
        }
        return;
        // add effect later
    }
}
