package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LegacyPlayerDataManager {

    private VirtualCrates plugin;
    private MyConfig playerdata;
    public LegacyPlayerDataManager(VirtualCrates instance) {
        this.plugin = instance;
        this.playerdata = plugin.getPlayerdata();
    }

    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public void giveCrate(Player p, Crate crate, int amount) {
        playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), getAmount(p, crate) + amount);
        playerdata.saveConfig();
        playerdata.reloadConfig();
    }
    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public void takeCrate(Player p, Crate crate, int amount) {
        if(getAmount(p, crate) < amount) { // if taking more than balance, set to zero
            System.out.print("Resetting player to Zero due to Negative Balance");
            playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), 0);
            playerdata.saveConfig();
            return;
        }

        playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), getAmount(p, crate) - amount);
        playerdata.saveConfig();
        playerdata.reloadConfig();
    }

    /**
     *
     * @param p - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public void giveCrate(OfflinePlayer p, Crate crate, int amount) {
        playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), getAmount(p, crate) + amount);
        playerdata.saveConfig();
        playerdata.reloadConfig();
    }

    /**
     *
     * @param p - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public void takeCrate(OfflinePlayer p, Crate crate, int amount) {
        if(getAmount(p, crate) < amount) { // if taking more than balance, set to zero
            playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), 0);
            playerdata.saveConfig();
            return;
        }

        playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), getAmount(p, crate) - amount);
        playerdata.saveConfig();
        playerdata.reloadConfig();
    }

    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @return get the amount of keys player has.
     *
     */
    public int getAmount(Player p, Crate crate) {
        if(playerdata.contains("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName())) {
            return playerdata.getInt("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName());
        } else {
            playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), 0);
            playerdata.saveConfig();
            playerdata.reloadConfig();
        }
        return 0;

    }

    public int getAmount(OfflinePlayer p, Crate crate) {
        if(playerdata.contains("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName())) {
            return playerdata.getInt("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName());
        } else {
            playerdata.set("playerdata." + p.getUniqueId().toString() + "." + crate.getCrateName(), 0);
            playerdata.saveConfig();
            playerdata.reloadConfig();
        }
        return 0;
    }

}
