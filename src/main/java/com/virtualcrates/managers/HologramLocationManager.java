package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.util.FormatedLocation;
import org.bukkit.Location;

import java.util.ArrayList;

public class HologramLocationManager {

    public VirtualCrates plugin;
    private MyConfig config;
    private MyConfig crateLocations;
    private FormatedLocation formatedLocation;
    public HologramLocationManager(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        crateLocations = plugin.getCratelocations();
        formatedLocation = new FormatedLocation();
    }

    /**
     * Adds a location to the crate locations to keep track of where holograms are located.
     * @param location
     */
    public void addHologramLocation(Location location) {

        if(!crateLocations.contains("hologramlocations")) {

            ArrayList<String> locations = new ArrayList<String>();
            locations.add(formatedLocation.getFormattedLocation(location));

            crateLocations.set("hologramlocations", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("hologramlocations");

        locations.add(formatedLocation.getFormattedLocation(location));
        crateLocations.set("hologramlocations", locations.toArray());
        crateLocations.saveConfig();
        crateLocations.reloadConfig();
    }

    public void deleteHologramLocation(Location location) {

        if(!crateLocations.contains("hologramlocations")) {
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("hologramlocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            locations.remove(formatedLocation.getFormattedLocation(location));
            crateLocations.set("hologramlocations", locations.toArray());
            crateLocations.saveConfig();
            crateLocations.reloadConfig();
        }
    }

    /**
     * @return lists holograms
     */
    public ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        locations.clear();

        if(crateLocations.contains("hologramlocations")) {
            ArrayList<String> formattedLocations = (ArrayList<String>) crateLocations.getList("hologramlocations");
            for(String string : formattedLocations) {
                locations.add(formatedLocation.getLocationFromFormattedString(string));
            }
        }

        return locations;
    }

    /**
     * @param location - location queiry
     * @returns boolean whether it is a valid location.
     */
    public boolean isHologramLocation(Location location) {

        if(!crateLocations.contains("hologramlocations")) {
            return false;
        }

        ArrayList<String> locations = (ArrayList<String>) crateLocations.getList("hologramlocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            return true;
        }

        return false;
    }


}
