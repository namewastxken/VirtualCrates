package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.util.FormatedLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateLocationManager {

    public VirtualCrates plugin;
    private MyConfig config;
    private MyConfig crateLocations;
    private FormatedLocation formatedLocation;
    private List<String> settingNPC;
    public CrateLocationManager(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        crateLocations = plugin.getCratelocations();
        formatedLocation = new FormatedLocation();
        settingNPC = new ArrayList<String>();
    }

    /**
     * Adds new crate location
     * @param location
     */
    public void setCrateLocation(Location location) {

        if(!crateLocations.contains("cratelocations")) {

            ArrayList<String> locations = new ArrayList<String>();
            locations.add(formatedLocation.getFormattedLocation(location));

            crateLocations.set("cratelocations", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("cratelocations");

        locations.add(formatedLocation.getFormattedLocation(location));
        crateLocations.set("cratelocations", locations.toArray());
        crateLocations.saveConfig();
        crateLocations.reloadConfig();
    }

    /**
     * Removes crate location
     * @param location
     */
    public void deleteCrateLocation(Location location) {

        if(!crateLocations.contains("cratelocations")) {
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("cratelocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            locations.remove(formatedLocation.getFormattedLocation(location));
            crateLocations.set("cratelocations", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
        }
    }

    /**
     * Gets list of Locations.
     * @return
     */
    public ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        locations.clear();

        if(crateLocations.contains("cratelocations")) {
            ArrayList<String> formattedLocations = (ArrayList<String>) crateLocations.getList("cratelocations");
            for(String string : formattedLocations) {
                locations.add(formatedLocation.getLocationFromFormattedString(string));
            }
        }

        return locations;
    }

    /**
     * Boolean of whether crate is stored.
     * @param location
     * @return
     */
    public boolean isCrateLocation(Location location) {

        if(!crateLocations.contains("cratelocations")) {
            return false;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("cratelocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            return true;
        }

        return false;
    }

    /**
     * Adds new NPC location
     * @param npc
     */
    public void setNPCLocation(NPC npc) {

        if(!crateLocations.contains("npcs")) {
            ArrayList<Integer> locations = new ArrayList<Integer>();
            locations.add(npc.getId());

            crateLocations.set("npcs", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
            return;
        }

        ArrayList<Integer> locations = (ArrayList<Integer>) crateLocations.getList("npcs");

        locations.add(npc.getId());
        crateLocations.set("npcs", locations.toArray());
        crateLocations.saveConfig();
        crateLocations.reloadConfig();
    }

    /**
     * Removes crate location
     * @param npc
     */
    public void deleteNPCLocation(NPC npc) {

        if(!crateLocations.contains("npcs")) {
            return;
        }

        ArrayList<Integer> locations = (ArrayList<Integer>) crateLocations.getList("npcs");

        if(locations.contains(npc.getId())) {
            locations.remove(new Integer(npc.getId()));
            crateLocations.set("npcs", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
        }
    }

    /**
     * Gets list of Locations.
     * @return
     */
    public ArrayList<Location> getNPCLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        locations.clear();

        if(crateLocations.contains("npcs")) {
            ArrayList<Integer> formattedLocations = (ArrayList<Integer>) crateLocations.getList("npcs");
            for(Integer id : formattedLocations) {
                locations.add(CitizensAPI.getNPCRegistry().getById(id).getStoredLocation());
            }
        }

        return locations;
    }

    /**
     * Boolean of whether crate is stored.
     * @param npc
     * @return
     */
    public boolean isNPCLocation(NPC npc) {

        if(!crateLocations.contains("npcs")) {
            return false;
        }

        ArrayList<Integer> locations = (ArrayList<Integer>) crateLocations.getList("npcs");

        if(locations.contains(npc.getId())) {
            return true;
        }

        return false;
    }

    public void startNPCSelection(Player p) {
        settingNPC.add(p.getName());
    }

    public void stopNPCSelection(Player p) {
        settingNPC.remove(p.getName());
    }

    public boolean isSelectingNPC(Player p) {
        return settingNPC.contains(p.getName());
    }
}
