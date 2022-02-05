package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.AnimationType;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.event.CrateInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PhysicalBulkListener implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    private ArrayList<Location> crateLocations = new ArrayList<Location>();

    public PhysicalBulkListener(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
    }

    private boolean isItem(Crate crate) {
        for (String items : config.getConfigurationSection("items").getKeys(false)) {

            if (config.getString("items." + items + ".crate_name").equalsIgnoreCase(crate.getCrateName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isBulkEnabled(Crate crate) {
        for (String items : config.getConfigurationSection("items").getKeys(false)) {
            if (config.getString("items." + items + ".crate_name").equalsIgnoreCase(crate.getCrateName())) {
                return config.getBoolean("items." + items +".allow_bulk_open");
            }
        }
        return false;
    }

    private int getBulkAmount(Crate crate) {
        for (String items : config.getConfigurationSection("items").getKeys(false)) {
            if (config.getString("items." + items + ".crate_name").equalsIgnoreCase(crate.getCrateName())) {
                return config.getInt("items." + items +".bulk_amount");
            }
        }
        return 0;
    }

    private boolean needBulkAmount(Crate crate) {
        for (String items : config.getConfigurationSection("items").getKeys(false)) {
            if (config.getString("items." + items + ".crate_name").equalsIgnoreCase(crate.getCrateName())) {
                return config.getBoolean("items." + items +".require_bulk_amount_to_open");
            }
        }
        return false;
    }



    @SuppressWarnings("Duplicates")
    @EventHandler
    public void onInteract(CrateInteractEvent e) {

        Player p = e.getPlayer();
        Crate crate = e.getCrate();
CrateAPI.getBlockCrateRegistrar().getCrates(e.getLocation());
        final int amount = p.getItemInHand().getAmount();

        if(crateLocations.size() != CrateAPI.getBlockCrateRegistrar().getLocations().size()) {
            crateLocations.clear();
            crateLocations.addAll(CrateAPI.getBlockCrateRegistrar().getLocations());
        }

        for(Location crateLocation : crateLocations) {

            if(crateLocation.getWorld() != p.getWorld()) {
                continue;
            }

            if(crateLocation.distance(e.getLocation()) > 5) {
                continue;
            }

            if (!isItem(crate)) {
                return;
            }

            if(!isBulkEnabled(crate)) {
                return;
            }

            if(!p.isSneaking()) {
                return;
            }

            if(!p.getItemInHand().isSimilar(crate.getItem())) {
                return;
            }

            if(CrateAPI.getBlockCrateRegistrar().getFirstCrate(e.getLocation()) != crate) {
                return;
            }


            e.setCancelled(true);
            final AnimationType oldAnimation = crate.getAnimationType();
            crate.setAnimationType(AnimationType.NONE);

            new BukkitRunnable() {
                public void run() {
                    int bulk = getBulkAmount(crate);

                    if (p.getItemInHand().getAmount() - bulk > 0) {
                        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - bulk);
                    } else {
                        p.setItemInHand(null);
                    }

                    if (amount < bulk) {

                        if(needBulkAmount(crate)) {

                            p.sendMessage(config.getString("messages.must_have_bulk_amount").replaceAll("&", "§").replaceAll("%amount%", bulk +""));

                            if(amount >= 1) {
                                ItemStack crateItem = crate.getItem().clone();
                                crateItem.setAmount(amount);
                                p.getInventory().addItem(crateItem);
                            }
                            return;
                        }

                        for (int i = 0; i < amount; i++) {
                            crate.open(p, false);
                            /*
                            for (Reward reward : crate.generatePrizes(p)) {
                                if (reward.getItems() != null && reward.getItems().size() >= 1) {
                                    for (ItemStack item : reward.getItems(p)) {
                                        p.getInventory().addItem(item);
                                        p.sendMessage(reward.getOpenMessage());
                                    }
                                }

                                if (reward.getCommands() != null && reward.getCommands().size() >= 1) {
                                    for (String command : reward.getCommands(p)) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player\\}", p.getName()));
                                        p.sendMessage(reward.getOpenMessage());
                                    }
                                }

                                crate.getOpenMessage().run(p, reward.getDisplayItem(), crate);
                                for (Player broadcastPlayer : Bukkit.getOnlinePlayers()) {
                                    crate.getBroadcast().run(broadcastPlayer, reward.getDisplayItem(), crate);
                                }
                            }
                            */ // Old method = complete shit
                        }
                        crate.setAnimationType(oldAnimation);
                        return;
                    }

                    if (amount >= bulk) {
                        for (int i = 0; i < bulk; i++) {
                            crate.open(p, false);
                          /*
                            for (Reward reward : crate.generatePrizes(p)) {


                                for (String string : reward.getOpenMessage()) {
                                    p.sendMessage(string);
                                }

                                if (reward.getItems().size() >= 1 && reward.getItems() != null) {
                                    for (ItemStack item : reward.getItems(p)) {
                                        p.getInventory().addItem(item);
                                    }
                                }

                                if (reward.getCommands().size() >= 1 && reward.getCommands() != null) {
                                    for (String command : reward.getCommands(p)) {

                                        if (command.startsWith("/")) {
                                            String cmd = command.substring(1);
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("\\{player\\}", p.getName()));
                                        } else {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player\\}", p.getName()));
                                        }
                                    }

                                    crate.getOpenMessage().run(p, reward.getDisplayItem(), crate);
                                    for (Player broadcastPlayer : Bukkit.getOnlinePlayers()) {
                                        crate.getBroadcast().run(broadcastPlayer, reward.getDisplayItem(), crate);
                                    }
                                }
                            }
                           */ // Old method complete shit.
                        }
                        crate.setAnimationType(oldAnimation);
                        return;
                    }
                }
            }.runTaskAsynchronously(plugin);


            return;
        }
    }
}
