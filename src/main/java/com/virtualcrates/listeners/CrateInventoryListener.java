package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.enums.CooldownType;
import com.virtualcrates.managers.CooldownManager;
import com.virtualcrates.managers.PlayerDataManager;
import com.virtualcrates.objects.Cooldown;
import com.virtualcrates.util.ItemsUtil;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.AnimationType;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class CrateInventoryListener implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;
    private ItemsUtil itemUtil;
    private CooldownManager cooldownManager;

    public CrateInventoryListener(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        playerDataManager = plugin.getPlayerDataManager();
        itemUtil = plugin.getItemUtil();
        cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if(e.getInventory() == null || e.getClickedInventory() == null) { // Detect and return whether if inventory clicked is real.
            return;
        }

        if(!e.getClickedInventory().getTitle().equalsIgnoreCase(config.getString("inventory.title").replaceAll("&", "§"))) { // detect if its virtual crate inv
            return;
        }

        e.setCancelled(true);

        // Might make this more efficient
        for(String crate : config.getConfigurationSection("items").getKeys(false)) {
            for(String crates : config.getConfigurationSection("items").getKeys(false)) {
                if(itemUtil.parseCrateItemFromConfig(p, crates) != null) {
                    if(e.getSlot() == config.getInt("items." + crate + ".slot")) {
                        Crate crateObj = CrateAPI.getInstance().getCrateRegistrar().getCrate(config.getString("items." + crate + ".crate_name"));
                         // Is Crate Key
                        if(e.getClick() == ClickType.RIGHT) {
                            if(config.getString("actions.right_click").equalsIgnoreCase("OPEN_CRATE")) {
                                if(playerDataManager.getAmount(p, crateObj) >= 1) {
                                    playerDataManager.takeCrate(p, crateObj, 1);
                                    if(crateObj.hasConfirmationToggle()) {
                                        p.closeInventory();
                                    } else {
                                        if(crateObj.getAnimationType() == AnimationType.NONE) {
                                            new BukkitRunnable() {
                                                public void run() {
                                                    if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(config.getString("inventory.title").replaceAll("&", "§"))) {
                                                        p.getOpenInventory().getTopInventory().setContents(plugin.getCrateInventoryManager().getVirtualCrates(p).getContents());
                                                    }
                                                }
                                            }.runTaskLaterAsynchronously(plugin, 5);
                                        }
                                    }
                                    CrateAPI.getInstance().getCrateRegistrar().tryOpen(crateObj, p, p.getLocation(), false);
                                } else {
                                    p.sendMessage(config.getString("messages.insufficient_crates").replaceAll("%crate%", crateObj.getDisplayName()).replaceAll("&", "§"));
                                    return;
                                }
                                return;
                            }

                            if(config.getString("actions.right_click").equalsIgnoreCase("PREVIEW_CRATE")) {
                                crateObj.preview(p);
                                return;
                            }
                            return;
                        }

                        if(e.getClick() == ClickType.LEFT) {
                            if (config.getString("actions.left_click").equalsIgnoreCase("OPEN_CRATE")) {
                                if (playerDataManager.getAmount(p, crateObj) >= 1) {
                                    //p.sendMessage(config.getString("messages.opened_crate").replaceAll("%crate%", crateObj.getDisplayName()).replaceAll("&", "§"));
                                    // p.sendMessage(crateObj.getOpenMessage().serialize());
                                    playerDataManager.takeCrate(p, crateObj, 1);
                                    if(crateObj.hasConfirmationToggle()) {
                                        p.closeInventory();
                                    } else {
                                        if(crateObj.getAnimationType() == AnimationType.NONE) {
                                            new BukkitRunnable() {
                                                public void run() {
                                                    if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(config.getString("inventory.title").replaceAll("&", "§"))) {
                                                        p.getOpenInventory().getTopInventory().setContents(plugin.getCrateInventoryManager().getVirtualCrates(p).getContents());
                                                    }
                                                }
                                            }.runTaskLaterAsynchronously(plugin, 5);
                                        }
                                    }
                                    CrateAPI.getInstance().getCrateRegistrar().tryOpen(crateObj, p, p.getLocation(), false);
                                } else {

                                    p.sendMessage(config.getString("messages.insufficient_crates").replaceAll("%crate%", crateObj.getCrateName()).replaceAll("&", "§"));
                                    p.playSound(p.getLocation(), Sound.valueOf(config.getString("sounds.insufficient_crates.sound_name")), config.getInt("sounds.insufficient_crates.volume"), config.getInt("sounds.insufficient_crates.pitch"));
                                    return;
                                }
                            }

                            if (config.getString("actions.left_click").equalsIgnoreCase("PREVIEW_CRATE")) {
                                crateObj.preview(p);
                                return;
                            }
                            return;
                        }

                        if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {

                            if(!config.getBoolean("items." + crate + ".allow_bulk_open")) {
                                return;
                            }

                            int bulk = config.getInt("items." + crate + ".bulk_amount");

                            int delay = config.getInt("cooldowns.bulk_open");

                            if(delay > 0) {
                                if(!p.hasPermission("virtualcrates.bulkcooldown.bypass")) {
                                    if (cooldownManager.onCooldown(p, CooldownType.BULK_OPEN)) {
                                        p.sendMessage(config.getString("messages.on_cooldown").replaceAll("&", "§").replaceAll("%delay%", cooldownManager.getNiceDouble(cooldownManager.getTimeLeftOnCooldown(p, CooldownType.BULK_OPEN))));
                                        return;
                                    }
                                    cooldownManager.createCooldown(new Cooldown(p, CooldownType.BULK_OPEN), delay);
                                }
                            }

                            final AnimationType oldAnimation = crateObj.getAnimationType();

                            if(playerDataManager.getAmount(p, crateObj) == 0) {
                                p.sendMessage(config.getString("messages.insufficient_crates").replaceAll("%crate%", crateObj.getCrateName()).replaceAll("&", "§"));
                                p.playSound(p.getLocation(), Sound.valueOf(config.getString("sounds.insufficient_crates.sound_name")), config.getInt("sounds.insufficient_crates.volume"), config.getInt("sounds.insufficient_crates.pitch"));
                                return;
                            }

                            new BukkitRunnable() {
                                public void run() {
                                    if (playerDataManager.getAmount(p, crateObj) < bulk) {
                                        if(config.getBoolean("items." + crate + ".require_bulk_amount_to_open")) {
                                            p.sendMessage(config.getString("messages.must_have_bulk_amount").replaceAll("&", "§").replaceAll("%amount%", config.getInt("items." + crate + ".bulk_amount") + ""));
                                            return;
                                        }
                                        crateObj.setAnimationType(AnimationType.NONE);

                                        for (int i = 0; i < playerDataManager.getAmount(p, crateObj); i++) {
                                            crateObj.open(p, false);
                                        }

                                        playerDataManager.takeCrate(p, crateObj, playerDataManager.getAmount(p, crateObj));
                                        crateObj.setAnimationType(oldAnimation);
                                        new BukkitRunnable() {
                                            public void run() {
                                                if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(config.getString("inventory.title").replaceAll("&", "§"))) {
                                                    p.getOpenInventory().getTopInventory().setContents(plugin.getCrateInventoryManager().getVirtualCrates(p).getContents());
                                                }
                                            }
                                        }.runTaskLaterAsynchronously(plugin, 5);
                                       // p.updateInventory();
                                        return;
                                    }

                                    if(playerDataManager.getAmount(p, crateObj) >= bulk) {
                                        crateObj.setAnimationType(AnimationType.NONE);

                                        for(int i = 0; i < bulk; i++) {
                                            crateObj.open(p, false);
                                        }

                                        //playerDataManager.takeCrate(p, crateObj, bulk);
                                        //p.updateInventory();

                                        playerDataManager.takeCrate(p, crateObj, bulk);
                                        crateObj.setAnimationType(oldAnimation);
                                        new BukkitRunnable() {
                                            public void run() {
                                                if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(config.getString("inventory.title").replaceAll("&", "§"))) {
                                                    p.getOpenInventory().getTopInventory().setContents(plugin.getCrateInventoryManager().getVirtualCrates(p).getContents());
                                                }
                                            }
                                        }.runTaskLaterAsynchronously(plugin, 5);
                                        return;
                                    }
                                }
                            }.runTaskAsynchronously(plugin);
                        }
                        return;
                    }
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "Could not create item for: " + crates);
                    continue;
                }
            }
        }

    }


}
