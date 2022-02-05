package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerDataManager {

    private VirtualCrates plugin;
    private MyConfig legacyData;
    private MyConfig config;
    private HashMap<String, YamlConfiguration> dataFiles;
    public PlayerDataManager(VirtualCrates instance) {
        this.plugin = instance;
        this.legacyData = plugin.getPlayerdata();
        this.config = plugin.getOptions();
        this.dataFiles = new HashMap<>();
    }

    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public void giveCrate(Player p, Crate crate, int amount) {
        new BukkitRunnable() {
            public void run() {
                YamlConfiguration playerdata = getPlayerDataFile(p);
                File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");
                playerdata.set(crate.getCrateName(), getAmount(p, crate) + amount);
                /*
                try {
                    playerdata.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */
            }
        }.runTaskAsynchronously(plugin);
    }
    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public void takeCrate(Player p, Crate crate, int amount) {

        new BukkitRunnable() {
            public void run() {
                YamlConfiguration playerdata = getPlayerDataFile(p);
                File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");

                //System.out.println("Crate Balance: " + getAmount(p, crate));
                //System.out.println("Amunt Taken: " + amount);

                if(getAmount(p, crate) < amount) { // if taking more than balance, set to zero
                    System.out.println("Resetting player to Zero due to Negative Balance");
                    playerdata.set(crate.getCrateName(), 0);
                    /*
                    try {
                        playerdata.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     */
                    return;
                }

                playerdata.set(crate.getCrateName(), getAmount(p, crate) - amount);
                /*
                try {
                    playerdata.save(file);
                    //System.out.println("Saved Amount: " + getAmount(p, crate));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     *
     * @param p - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates given
     *
     */
    public void giveCrate(OfflinePlayer p, Crate crate, int amount) {

        new BukkitRunnable() {
            public void run() {
                YamlConfiguration playerdata = getPlayerDataFile(p);
                File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");

                playerdata.set(crate.getCrateName(), getAmount(p, crate) + amount);
                /*
                try {
                    playerdata.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     *
     * @param p - OfflinePlayer
     * @param crate - CrateReloaded crate object
     * @param amount - Amount of crates taken
     *
     */
    public void takeCrate(OfflinePlayer p, Crate crate, int amount) {
        YamlConfiguration playerdata = getPlayerDataFile(p);
        File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");

        // System.out.print("Crate Balance: " + getAmount(p, crate));
        // System.out.print("Amunt Taken: " + amount);

        new BukkitRunnable() {
            public void run() {
                if(getAmount(p, crate) < amount) { // if taking more than balance, set to zero
                    playerdata.set(crate.getCrateName(), 0);
                  /*
                    try {
                        playerdata.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   */
                    return;
                }

                playerdata.set(crate.getCrateName(), getAmount(p, crate) - amount);
                /*
                try {
                    playerdata.save(file);
               //     System.out.print("Saved Amount: " + getAmount(p, crate));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @return get the amount of keys player has.
     *
     */
    public int getAmount(Player p, Crate crate) {
        YamlConfiguration playerdata = getPlayerDataFile(p);
        File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");

        if(playerdata.contains(crate.getCrateName())) {
            return playerdata.getInt(crate.getCrateName());
        } else {
            playerdata.set(crate.getCrateName(), 0);
            /*
            try {
                playerdata.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
             */
        }
        return 0;
    }

    /**
     *
     * @param p - Player
     * @param crate - CrateReloaded crate object
     * @return get the amount of keys an offlineplayer has.
     *
     */
    public int getAmount(OfflinePlayer p, Crate crate) {
        YamlConfiguration playerdata = getPlayerDataFile(p);
        File file = new File(plugin.getDataFolder() + "/playerdata/", p.getUniqueId().toString() + ".yml");

        if(playerdata.contains(crate.getCrateName())) {
            return playerdata.getInt(crate.getCrateName());
        } else {
            playerdata.set(crate.getCrateName(), 0);
            /*
            try {
                playerdata.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
             */
        }
        return 0;
    }

    /**
     * Retrieves the playerdata file of a player for manipulation.
     * @param player - player
     * @return Datafile of the player
     */
    public YamlConfiguration getPlayerDataFile(Player player) {

        if(hasCachedDataFile(player.getUniqueId().toString())) {
            return dataFiles.get(player.getUniqueId().toString());
        }

        File file = new File(plugin.getDataFolder() + "/playerdata/", player.getUniqueId().toString() + ".yml");
        if(!file.exists()) {
            createPlayerDataFile(player);
        }

        dataFiles.put(player.getUniqueId().toString(), YamlConfiguration.loadConfiguration(file));
        return dataFiles.get(player.getUniqueId().toString());
    }

    public YamlConfiguration getPlayerDataFile(OfflinePlayer player) {
        if(hasCachedDataFile(player.getUniqueId().toString())) {
            return dataFiles.get(player.getUniqueId().toString());
        }

        File file = new File(plugin.getDataFolder() + "/playerdata/", player.getUniqueId().toString() + ".yml");
        if(!file.exists()) {
            createPlayerDataFile(player);
        }

        dataFiles.put(player.getUniqueId().toString(), YamlConfiguration.loadConfiguration(file));
        return dataFiles.get(player.getUniqueId().toString());
    }


    /**
     * Creates a playerdata yml for that specific player.
     * @param player - Player
     */
    public void createPlayerDataFile(Player player) {
        new BukkitRunnable() {
            public void run() {
                String string = player.getUniqueId().toString();
                File file = new File(plugin.getDataFolder() + "/playerdata/", string + ".yml");
                if(!file.exists()) {
                    System.out.println("Creating file: '" + string + ".yml'.");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        System.out.println("An error occured while creating file for: " + string);
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Creates a playerdata yml for that specific player.
     * @param player - Player
     */
    public void createPlayerDataFile(OfflinePlayer player) {
        new BukkitRunnable() {
            public void run() {
                String string = player.getUniqueId().toString();
                File file = new File(plugin.getDataFolder() + "/playerdata/", string + ".yml");
                if(!file.exists()) {
                    System.out.println("Creating file: '" + string + ".yml'.");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        System.out.println("An error occured while creating file for: " + string);
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Creats player data folder for files.
     */
    public void createPlayerDataFolder() {
        File playerDataFolder = new File(plugin.getDataFolder() + "/playerdata");
        if(!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    /**
     * Method to iterate to prevent concurrent modify exception.
     */
    private boolean hasCachedDataFile(String uuid) {
        for(String key : dataFiles.keySet()) {
            if(key.equalsIgnoreCase(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method to migrate old data structure into a more organized and optimized way.
     */
    public void migrateNewData() {

        if(config.contains("legacy-data-migrated")) {
            return;
        }
        System.out.println("Starting migration of data from legacy playerdata.yml");
        Long startTime = System.currentTimeMillis();
        new BukkitRunnable() {
            public void run() {
                if(!legacyData.contains("playerdata")) {
                    config.set("legacy-data-migrated", true);
                    config.saveConfig();
                    return;
                }

                for(String string : legacyData.getConfigurationSection("playerdata").getKeys(false)) {
                    File playerDataFolder = new File(plugin.getDataFolder() + "/playerdata");
                    if(!playerDataFolder.exists()) {
                        playerDataFolder.mkdirs();
                    }

                    File file = new File(plugin.getDataFolder() + "/playerdata/", string + ".yml");
                    if(!file.exists()) {
                        //System.out.println("Creating file: '" + string + ".yml'.");
                        try {
                            file.createNewFile();
                            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

                            for(String playerDataPaths : legacyData.getConfigurationSection("playerdata." + string).getKeys(false)) {
                                data.set(playerDataPaths, legacyData.get("playerdata." + string + "." + playerDataPaths));
                            }

                            data.save(file);
                            //     System.out.println("Successfully created file: '" + string + ".yml'.");
                        } catch (IOException e) {
                            System.out.println("An error occured while creating file for: " + string);
                            e.printStackTrace();
                        }
                    }
                }
                float ms = System.currentTimeMillis() - startTime;
                System.out.println("Migration complete, completion in " + ms + " ms.");
                config.set("legacy-data-migrated", true);
                config.saveConfig();
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Task created to save playerdata periodically
     */
    public void startSaveTask() {
        System.out.print("[VirtualCrates] Starting playerdata save task.");
        new BukkitRunnable() {
            public void run() {
                if(config.getBoolean("log.enable_saved_plyerdata_logging")) {
                    System.out.println("[VirtualCrates] Saving playerdata...");
                }
                for(String string : dataFiles.keySet()) {
                    File file = new File(plugin.getDataFolder() + "/playerdata/", string + ".yml");
                    try {
                        dataFiles.get(string).save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20, 120 * 20);

    }

    /**
     * Manually saves all data
     */
    public void saveData() {
        if(config.getBoolean("log.enable_saved_plyerdata_logging")) {
            System.out.println("[VirtualCrates] Saving playerdata...");
        }
        for(String string : dataFiles.keySet()) {
            File file = new File(plugin.getDataFolder() + "/playerdata/", string + ".yml");
            try {
                dataFiles.get(string).save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
