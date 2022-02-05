package com.virtualcrates.papi;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.PlayerDataManager;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Placeholders extends PlaceholderExpansion {

    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;

    public Placeholders(VirtualCrates instance) {
        this.plugin = instance;
        this.config = this.plugin.getOptions();
        this.playerDataManager = this.plugin.getPlayerDataManager();
    }

    public int getPhysicalCrates(Player p, Crate crate) {
        int counted = 0;
        ItemStack crateItem = crate.getItem();

        for(ItemStack item : p.getInventory().getContents()) {

            if(item == null) {
                continue;
            }

            if(item.isSimilar(crateItem)) {
                counted+= item.getAmount();
            }
        }

        return counted;
    }

    private ArrayList<Crate> getCrates() {
        ArrayList<Crate> crates = new ArrayList<Crate>();

        crates.clear();

        for(String item : config.getConfigurationSection("items").getKeys(false)) {
            crates.add(CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + item + ".crate_name")));
        }

        return crates;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        for(Crate crate : CrateAPI.getCrateRegistrar().getCrates()) {
            if(identifier.equalsIgnoreCase( crate.getCrateName() + "_physical")) {
                return getPhysicalCrates(p, crate) + "";
            }

            if(identifier.equalsIgnoreCase( crate.getCrateName() + "_virtual")) {
                return playerDataManager.getAmount(p, crate) + "";
            }
        }

        return null;
    }

    @Override
    public String getIdentifier() {
        return "virtualcrates";
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public boolean persist() {
        return true;
    }
}
