package com.virtualcrates.api;

import com.virtualcrates.VirtualCrates;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VirtualCratesAPI  {

    /**
     *
     * @param player - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public static void giveCrate(Player player, Crate crate, int amount) {
        getInstance().getPlayerDataManager().giveCrate(player, crate, amount);
    }
    /**
     *
     * @param player - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public static void takeCrate(Player player, Crate crate, int amount) {
        getInstance().getPlayerDataManager().takeCrate(player, crate, amount);
    }

    /**
     *
     * @param offlinePlayer - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public static void giveCrate(OfflinePlayer offlinePlayer, Crate crate, int amount) {
        getInstance().getPlayerDataManager().giveCrate(offlinePlayer, crate, amount);
    }

    /**
     *
     * @param offlinePlayer - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public static void takeCrate(OfflinePlayer offlinePlayer, Crate crate, int amount) {
        getInstance().getPlayerDataManager().takeCrate(offlinePlayer, crate, amount);
    }

    /**
     *
     * @param player - Player
     * @param crate - CrateReloaded crate object
     * @return get the amount of keys player has.
     *
     */
    public static final int getVirtualCratesBalance(Player player, Crate crate) {
        return getInstance().getPlayerDataManager().getAmount(player, crate);
    }

    /**
     *
     * @param offlinePlayer - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @return get the amount of keys player has.
     *
     */
    public static final int getVirtualCratesBalance(OfflinePlayer offlinePlayer, Crate crate) {
        return getInstance().getPlayerDataManager().getAmount(offlinePlayer, crate);
    }

    public static VirtualCrates getInstance() {
        return (VirtualCrates) Bukkit.getPluginManager().getPlugin("VirtualCrates");
    }

}
