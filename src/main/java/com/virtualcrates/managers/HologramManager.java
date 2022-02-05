package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;

import java.util.ArrayList;

public class HologramManager {

    private VirtualCrates plugin;
    private MyConfig config;
    private MyConfig cratelocations;
    private CrateLocationManager crateLocationManager;
    public HologramManager(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        cratelocations = plugin.getCratelocations();
        crateLocationManager = plugin.getCrateLocationManager();
    }

    /**
     * Creates holograms for each stored crate location.
     */
    public void createHolograms() {
        for(Location location : plugin.getHologramLocationManager().getLocations()) {
            if(config.getBoolean("holograms.enabled")) {

                Location hologram = location.clone();

                Hologram holo = HologramsAPI.createHologram(plugin, hologram);

                ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

                for(String hologramLine : hologramList) {
                    holo.appendTextLine(hologramLine.replaceAll("&", "§"));
                }
            }
        }
    }

    /**
     * Stores and creates hologram for location.
     * @param location
     */
    public void addHologram(Location location) {
        if(config.getBoolean("holograms.enabled")) {
            Location hologram = location.clone();
            hologram.add(config.getDouble("holograms.offset.x"), config.getDouble("holograms.offset.y"), config.getDouble("holograms.offset.z"));

            Hologram holo = HologramsAPI.createHologram(plugin, hologram);

            plugin.getHologramLocationManager().addHologramLocation(hologram);

            ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

            for(String hologramLine : hologramList) {
                holo.appendTextLine(hologramLine.replaceAll("&", "§"));
            }
        }
    }

    /**
     * Stores and creates hologram for location.
     * @param location
     */
    public void addNPCHologram(Location location) {
        if(config.getBoolean("holograms.enabled")) {
            Location hologram = location.clone();
            hologram.add(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));

            Hologram holo = HologramsAPI.createHologram(plugin, hologram);

            plugin.getHologramLocationManager().addHologramLocation(hologram);

            ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

            for(String hologramLine : hologramList) {
                holo.appendTextLine(hologramLine.replaceAll("&", "§"));
            }
        }
    }

    /**
     * removes a hologram.
     * @param location
     */
    public void removeHologram(Location location) {
        for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if (hologram.getLocation().distance(location) <= (config.getDouble("holograms.offset.y") + config.getDouble("holograms.offset.x") + config.getDouble("holograms.offset.z")) || hologram.getLocation().distance(location) <= (config.getDouble("holograms.npc_offset.y") + config.getDouble("holograms.npc_offset.x") + config.getDouble("holograms.npc_offset.z"))) {
                hologram.delete();
                plugin.getHologramLocationManager().deleteHologramLocation(hologram.getLocation());
            }
        }
    }



}
