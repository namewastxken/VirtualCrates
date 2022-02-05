package com.virtualcrates.commands;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.PlayerDataManager;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ConvertCommand implements CommandExecutor {

    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;
    public ConvertCommand(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        playerDataManager = plugin.getPlayerDataManager();
    }

    private boolean hasPhysicalAmount(Player p, Crate crate, int amount) {

        ItemStack key = crate.getItem();
        int counted = 0;

        for(ItemStack item : p.getInventory().getContents()) {
            if(item == null) {
                continue;
            }
            if(item.isSimilar(key)) {
                counted+= item.getAmount();
            }
        }
        return counted >= amount;
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            if (args.length != 4) {
                sender.sendMessage("§c/convert <player> <key> <physical/virtual> <amount> - converts a key to either physical or virtual.");
                sender.sendMessage("§7§oExample: §f/convert FoodKey physical 10, this would convert 10 virutal FoodKey keys to physical.");
                return true;
            }
            Player t =  Bukkit.getPlayer(args[0]);
            String key = args[1];
            String type = args[2];
            Crate crate = CrateAPI.getCrateRegistrar().getCrate(key);

            if(t == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }

            if(crate == null) {
                sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", key));
                return true;
            }

            if(config.getBoolean("key_conversion.whitelist_enabled")) {
                ArrayList<String> whitelistedCrates = (ArrayList<String>) config.getList("key_conversion.whitelist");

                if(!whitelistedCrates.contains(crate.getCrateName())) {
                    sender.sendMessage(config.getString("messages.key_cant_be_converted").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()));
                    return true;
                }
            }

            try {
                int amount = Integer.parseInt(args[3]);
                int converted = 0;

                if(amount <= 0) {
                    sender.sendMessage("§cAmount must be greater than 0.");
                    return true;
                }

                if(type.equalsIgnoreCase("virtual") || type.equalsIgnoreCase("physical")) {

                    if(type.equalsIgnoreCase("virtual")) {

                        if(!hasPhysicalAmount(t, crate, amount)) {
                            sender.sendMessage(config.getString("messages.not_enough_to_convert").replaceAll("&", "§"));
                            return true;
                        }

                        for(ItemStack item : t.getInventory().getContents()) {

                            if(item == null) {
                                continue;
                            }

                            ItemStack crateItem = crate.getItem();
                            if(crate.getItem().isSimilar(item)) {
                                if(amount < item.getAmount()) {
                                    item.setAmount(item.getAmount() - amount);
                                    playerDataManager.giveCrate(t, crate, amount);
                                    sender.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", amount + ""));
                                    return true;
                                }

                                if(amount >= item.getAmount()) {
                                    converted+= item.getAmount();
                                    t.getInventory().removeItem(item);
                                    t.updateInventory();
                                    playerDataManager.giveCrate(t, crate, amount);
                                }

                            }

                        }
                        if(converted > 0) {
                            t.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", converted + ""));
                        } else {
                            t.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", converted + ""));
                        }
                        converted = 0;
                        return true;
                    }

                    if(type.equalsIgnoreCase("physical")) {

                        if(playerDataManager.getAmount(t, crate) < amount) {
                            t.sendMessage(config.getString("messages.insufficient_crates").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()));
                            return true;
                        }

                        playerDataManager.takeCrate(t, crate, amount);

                        for(int i = 0; i < amount; i++) {
                            if(t.getInventory().firstEmpty() == -1) {
                                t.getWorld().dropItemNaturally(t.getLocation(), crate.getItem());
                            } else {
                                t.getInventory().addItem(crate.getItem());
                            }
                        }

                        if(t.getInventory().firstEmpty() == -1) {
                            t.sendMessage(config.getString("messages.conversion_inventory_full").replaceAll("&", "§"));
                    }

                        t.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", amount + ""));
                        return true;
                    }

                } else {
                    sender.sendMessage("§cInvalid key type: Physical or Virtual.");
                    return true;
                }

            } catch (NumberFormatException err) {
                sender.sendMessage("§cYou must input a valid number.");
                return true;
            }

            return true;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("virtualcrates.convert")) {
            p.sendMessage("§cNo Permission.");
            return true;
        }

        if (args.length != 3) {
            p.sendMessage(config.getString("messages.convert_command_syntax").replaceAll("&", "§"));
            p.sendMessage(config.getString("messages.convert_command_syntax_example").replaceAll("&", "§"));
            return true;
        }

        String key = args[0];
        String type = args[1];
        Crate crate = CrateAPI.getCrateRegistrar().getCrate(key);

        if(crate == null) {
            p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", key));
            return true;
        }

        if(config.getBoolean("key_conversion.whitelist_enabled")) {
            ArrayList<String> whitelistedCrates = (ArrayList<String>) config.getList("key_conversion.whitelist");

            if(!whitelistedCrates.contains(crate.getCrateName())) {
                p.sendMessage(config.getString("messages.key_cant_be_converted").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()));
                return true;
            }
        }

        try {
            int amount = Integer.parseInt(args[2]);
            int converted = 0;

            if(amount <= 0) {
                p.sendMessage(config.getString("messages.conversion_amount_must_be_positive").replaceAll("&", "§"));
                return true;
            }

            if(type.equalsIgnoreCase("virtual") || type.equalsIgnoreCase("physical")) {

                if(type.equalsIgnoreCase("virtual")) {

                    if(!hasPhysicalAmount(p, crate, amount)) {
                        sender.sendMessage(config.getString("messages.not_enough_to_convert").replaceAll("&", "§"));
                        return true;
                    }

                    for(ItemStack item : p.getInventory().getContents()) {

                        if(item == null) {
                            continue;
                        }

                        if(converted == amount) {
                            p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", amount + ""));
                            return true;
                        }

                        ItemStack crateItem = crate.getItem();
                        if(crate.getItem().isSimilar(item)) {
                            if(amount < item.getAmount()) {
                                item.setAmount(item.getAmount() - amount);
                                playerDataManager.giveCrate(p, crate, amount);
                                p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", amount + ""));
                                return true;
                            }

                            if(amount >= item.getAmount()) {
                                converted+= item.getAmount();
                                playerDataManager.giveCrate(p, crate, item.getAmount());
                                p.getInventory().remove(item);
                                p.updateInventory();
                            }

                        }

                    }
                    if(converted > 0) {
                        p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", converted + ""));
                    } else {
                        p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", converted + ""));
                    }
                    converted = 0;
                    return true;
                }

                if(type.equalsIgnoreCase("physical")) {

                    if(playerDataManager.getAmount(p, crate) < amount) {
                        p.sendMessage(config.getString("messages.insufficient_crates").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()));
                        return true;
                    }

                    playerDataManager.takeCrate(p, crate, amount);

                    for(int i = 0; i < amount; i++) {
                        if(p.getInventory().firstEmpty() == -1) {
                            p.getWorld().dropItemNaturally(p.getLocation(), crate.getItem());
                        } else {
                            p.getInventory().addItem(crate.getItem());
                        }
                    }

                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(config.getString("messages.conversion_inventory_full").replaceAll("&", "§"));
                    }

                    p.sendMessage(config.getString("messages.converted_keys").replaceAll("&", "§").replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", amount + ""));
                    return true;
                }
            } else {
                try {
                    p.sendMessage("§cInvalid key type: Physical or Virtual.");
                } catch (NullPointerException er) {
                    return true;
                }
                return true;
            }

        } catch (NumberFormatException err) {
            p.sendMessage("§cYou must input a valid number.");
            return true;
        }

        return true;
    }
}
