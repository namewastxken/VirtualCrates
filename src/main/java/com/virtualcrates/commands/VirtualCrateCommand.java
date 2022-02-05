    package com.virtualcrates.commands;

    import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.CrateInventoryManager;
import com.virtualcrates.managers.CrateLocationManager;
import com.virtualcrates.managers.PlayerDataManager;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
    import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

    public class VirtualCrateCommand implements CommandExecutor {

    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;
    private CrateInventoryManager crateInventoryManager;
    private CrateLocationManager crateLocationManager;

    public VirtualCrateCommand(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.crateInventoryManager = plugin.getCrateInventoryManager();
        this.crateLocationManager = plugin.getCrateLocationManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {

            if(args.length == 0) {

                sender.sendMessage("§7§m-------------------------------------");
                sender.sendMessage("");
                if (sender.hasPermission("virtualcrates.admin")) {
                    sender.sendMessage("§a/virtualcrates giveall <key> <amount> §8- §7gives everyone a key.");
                    sender.sendMessage("§a/virtualcrates give <player> <key> <amount> §8- §7gives virtual balance player.");
                    sender.sendMessage("§a/virtualcrates remove <player> <key> <amount> §8- §7removes virtual balance from a player.");
                }
                sender.sendMessage("§a/virtualcrates get <player> <key> §8- §7retrieves a virtual balance from a player.");
                sender.sendMessage("");
                sender.sendMessage("§7§m-------------------------------------");
                return true;
            }


            if(args.length == 4) {

                // args: give player key amt
                // args: remove player key amt

                if(args[0].equalsIgnoreCase("give")) {
                    Player t = Bukkit.getPlayer(args[1]);
                    try {
                        int amount = Integer.parseInt(args[3]);
                        if (t != null) {
                            Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                            if (crate == null) {
                                sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                return true;
                            }

                            playerDataManager.giveCrate(t, crate, amount);
                            sender.sendMessage(config.getString("messages.gave_virtualkey").replaceAll("%player%", t.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                            if(!config.getString("messages.received_virtualkey_balance").equalsIgnoreCase("")) {
                                t.sendMessage(config.getString("messages.received_virtualkey_balance").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                            }
                        } else {
                            for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                if(off.getName().equalsIgnoreCase(args[1])) {
                                    Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                    if (crate == null) {
                                        sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                        return true;
                                    }

                                    playerDataManager.giveCrate(off, crate, amount);
                                    sender.sendMessage(config.getString("messages.gave_virtualkey").replaceAll("%player%", off.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                    return true;
                                }
                            }
                            sender.sendMessage("§cPlayer not found");
                            return true;
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage("§cYou must input a valid number.");
                    }
                }

                if(args[0].equalsIgnoreCase("remove")) {
                    Player t = Bukkit.getPlayer(args[1]);
                    try {
                        int amount = Integer.parseInt(args[3]);
                        if (t != null) {
                            Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                            if (crate == null) {
                                sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                return true;
                            }

                            playerDataManager.takeCrate(t, crate, amount);
                            sender.sendMessage(config.getString("messages.remove_virtualkey").replaceAll("%player%", t.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                            t.sendMessage(config.getString("messages.virtual_key_removed").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                            return true;
                        } else {
                            for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                if(off.getName().equalsIgnoreCase(args[1])) {
                                    Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                    if (crate == null) {
                                        sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                        return true;
                                    }

                                    playerDataManager.takeCrate(off, crate, amount);
                                    sender.sendMessage(config.getString("messages.remove_virtualkey").replaceAll("%player%", off.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                    return true;
                                }
                            }
                            sender.sendMessage("§cPlayer not found");
                            return true;
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage("§cYou must input a valid number.");
                    }
                }

            }

            if(args.length == 3) {

                if(args[0].equalsIgnoreCase("get")) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if(t != null) {
                        Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                        if (crate == null) {
                            sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                            return true;
                        }

                        sender.sendMessage(config.getString("messages.virtual_crate_balance").replaceAll("%player%", t.getName()).replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", playerDataManager.getAmount(t, crate) + "").replaceAll("&", "§"));
                        return true;
                    } else {
                        for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                            if(off.getName().equalsIgnoreCase(args[1])) {
                                Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                if (crate == null) {
                                    sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                    return true;
                                }

                                sender.sendMessage(config.getString("messages.virtual_crate_balance").replaceAll("%player%", off.getName()).replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", playerDataManager.getAmount(off, crate) + "").replaceAll("&", "§"));
                                return true;
                            }
                        }
                        sender.sendMessage("§cPlayer not found");
                        return true;
                    }

                }

                if(args[0].equalsIgnoreCase("giveall")) {

                    Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[1]);
                    try {
                        int amount = Integer.parseInt(args[2]);

                        if(amount <= 0) {
                            sender.sendMessage(config.getString("messages.conversion_amount_must_be_positive").replaceAll("&", "§"));
                            return true;
                        }

                        if (crate == null) {
                            sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                            return true;
                        }

                        if (giveAllCrate(crate, amount)) {
                            sender.sendMessage(config.getString("messages.gave_all_key").replaceAll("&", "§").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()));
                        } else {
                            sender.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(config.getString("messages.conversion_amount_must_be_positive").replaceAll("&", "§"));
                        return true;
                    }
                }
            }
            return true;
        }
        Player p = (Player) sender;

        if(args.length == 0) {

            if (p.hasPermission("virtualcrates.admin") || p.hasPermission("virtualcrates.view.commands")) {
                p.sendMessage("§7§m-------------------------------------");
                p.sendMessage("");
                if (p.hasPermission("virtualcrates.admin")) {
                    p.sendMessage("§a/virtualcrates reload §8 - §7Reloads the config.");
                    p.sendMessage("§a/virtualcrates setnpc §8 - §7Adds a virtual crate location in a citizens npc.");
                    p.sendMessage("§a/virtualcrates set §8 - §7adds a new crate location.");
                    p.sendMessage("§a/virtualcrates giveall <key> <amount> §8- §7gives everyone a key.");
                    p.sendMessage("§a/virtualcrates give <player> <key> <amount> §8- §7gives virtual balance player.");
                    p.sendMessage("§a/virtualcrates remove <player> <key> <amount> §8- §7removes virtual balance from a player.");
                    p.sendMessage("§a/virtualcrates get <player> <key> §8- §7retrieves a virtual balance from a player.");
                }

                if (p.hasPermission("virtualcrates.open")) {
                    p.sendMessage("§a/virtualcrates open §8 - §7opens the virtual crates gui.");
                }

                if (p.hasPermission("virtualcrates.convert")) {
                    p.sendMessage("§a/convert §8 - §7convert keys to physical or virtual.");
                }

                p.sendMessage("");
                 p.sendMessage("§7§m-------------------------------------");
                return true;

            }
        }
        if(!p.hasPermission("virtualcrates.admin") && !p.hasPermission("virtualcrates.open") && !p.hasPermission("virtualcrates.view.commands")) {
            p.sendMessage("§cNo Permission");
            return true;
        }

        if(p.hasPermission("virtualcrates.open")) {
            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("open")) {
                    p.openInventory(crateInventoryManager.getVirtualCrates(p));
                    return true;
                }
            }
        }

        if(p.hasPermission("virtualcrates.admin")) {
            if(args.length == 0) {
                p.sendMessage("§7§m-------------------------------------");
                p.sendMessage("");
                p.sendMessage("§a/virtualcrates reload §8 - §7Reloads the config.");
                p.sendMessage("§a/virtualcrates giveall <key> <amount> §8- §7gives everyone a key.");
                p.sendMessage("§a/virtualcrates open §8 - §7opens the virtual crates gui.");
                p.sendMessage("§a/virtualcrates set §8 - §7adds a new crate location.");
                p.sendMessage("§a/virtualcrates setnpc §8 - §7Adds a virtual crate location in a citizens npc.");
                p.sendMessage("§a/virtualcrates give <player> <key> <amount> §8- §7gives virtual balance to a player.");
                p.sendMessage("§a/virtualcrates remove <player> <key> <amount> §8- §7removes virtual balance from a player.");
                p.sendMessage("§a/virtualcrates get <player> <key> §8- §7retrieves a virtual balance from a player.");
                p.sendMessage("§a/convert §8 - §7convert keys to physical or virtual.");
                p.sendMessage("");
                p.sendMessage("§7§m-------------------------------------");
                return true;
            }

            if(args.length == 1) {
                if(p.hasPermission("virtualcrates.admin") || p.hasPermission("virtualcrates.open")) {
                    if (args[0].equalsIgnoreCase("open")) {
                        p.openInventory(crateInventoryManager.getVirtualCrates(p));
                        return true;
                    }
                } else {
                    p.sendMessage("§cNo Permission.");
                    return true;
                }

                if(p.hasPermission("virtualcrates.admin")) {

                    if(args[0].equalsIgnoreCase("reload")) {

                        plugin.getOptions().reloadConfig();
                        p.sendMessage(config.getString("messages.reload_config").replaceAll("&", "§"));
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("setnpc")) {
                        if (!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }

                        if(!plugin.canHaveNPC()) {
                            p.sendMessage("§cUnable to do this because Citizens is not installed.");
                            return true;
                        }

                        if(!crateLocationManager.isSelectingNPC(p)) {
                            crateLocationManager.startNPCSelection(p);
                            p.sendMessage(config.getString("messages.setting_npc").replaceAll("&", "§"));
                            return true;
                        } else {
                            crateLocationManager.stopNPCSelection(p);
                            p.sendMessage(config.getString("messages.cancelled_npc_selection").replaceAll("&", "§"));
                            return true;
                        }
                    }


                    if(args[0].equalsIgnoreCase("set")) {
                        if(!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }
                        Block block = getTargetBlock(p, 3);

                        if(block != null) {
                            if(crateLocationManager.isCrateLocation(block.getLocation())) {
                                p.sendMessage("§cCan't set virtual crate, as this is already a crate.");
                                return true;
                            }

                            crateLocationManager.setCrateLocation(block.getLocation());

                            if(plugin.getOptions().getBoolean("holograms.enabled")) {
                                plugin.getHologramManager().addHologram(block.getLocation());
                            }

                            p.sendMessage("§a§lVirtual Crates §7» §fSet virtual crate location.");
                            return true;
                        }
                        return true;
                    }
                }
            }

            if(args.length == 4) {

                // args: give player key amt
                // args: remove player key amt
                if(p.hasPermission("virtualcrates.admin")) {
                    if(args[0].equalsIgnoreCase("give")) {
                        if(!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }
                        Player t = Bukkit.getPlayer(args[1]);
                        try {
                            int amount = Integer.parseInt(args[3]);
                            if (t != null) {
                                Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                if (crate == null) {
                                    p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                    return true;
                                }

                                playerDataManager.giveCrate(t, crate, amount);
                                p.sendMessage(config.getString("messages.gave_virtualkey").replaceAll("%player%", t.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                if(!config.getString("messages.received_virtualkey_balance").equalsIgnoreCase("")) {
                                    t.sendMessage(config.getString("messages.received_virtualkey_balance").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                }
                            } else {
                                for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                    if(off.getName().equalsIgnoreCase(args[1])) {
                                        Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                        if (crate == null) {
                                            p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                            return true;
                                        }

                                        playerDataManager.giveCrate(off, crate, amount);
                                        p.sendMessage(config.getString("messages.gave_virtualkey").replaceAll("%player%", off.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                        return true;
                                    }
                                }
                                p.sendMessage("§cPlayer not found");
                                return true;
                            }
                        } catch (NumberFormatException ex) {
                            p.sendMessage("§cYou must input a valid number.");
                        }
                    }

                    if(args[0].equalsIgnoreCase("remove")) {
                        if(!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }
                        Player t = Bukkit.getPlayer(args[1]);
                        try {
                            int amount = Integer.parseInt(args[3]);
                            if (t != null) {
                                Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                if (crate == null) {
                                    p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                    return true;
                                }

                                playerDataManager.takeCrate(t, crate, amount);
                                p.sendMessage(config.getString("messages.remove_virtualkey").replaceAll("%player%", t.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                t.sendMessage(config.getString("messages.virtual_key_removed").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                return true;
                            } else {
                                for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                    if(off.getName().equalsIgnoreCase(args[1])) {
                                        Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                        if (crate == null) {
                                            p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                            return true;
                                        }

                                        playerDataManager.takeCrate(off, crate, amount);
                                        p.sendMessage(config.getString("messages.remove_virtualkey").replaceAll("%player%", off.getName()).replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()).replaceAll("&", "§"));
                                        return true;
                                    }
                                }
                                p.sendMessage("§cPlayer not found");
                                return true;
                            }
                        } catch (NumberFormatException ex) {
                            p.sendMessage("§cYou must input a valid number.");
                        }
                    }
                } else {
                    p.sendMessage("§cNo Permission");
                    return true;
                }
            }
            if(args.length == 3) {
                if(p.hasPermission("virtualcrates.admin")) {
                    if(args[0].equalsIgnoreCase("get")) {

                        if(!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }

                        Player t = Bukkit.getPlayer(args[1]);
                        if(t != null) {
                            Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                            if (crate == null) {
                                p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                return true;
                            }

                            p.sendMessage(config.getString("messages.virtual_crate_balance").replaceAll("%player%", t.getName()).replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", playerDataManager.getAmount(t, crate) + "").replaceAll("&", "§"));
                            return true;
                        } else {
                            for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                if(off.getName().equalsIgnoreCase(args[1])) {
                                    Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[2]);

                                    if (crate == null) {
                                        p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                        return true;
                                    }

                                    p.sendMessage(config.getString("messages.virtual_crate_balance").replaceAll("%player%", off.getName()).replaceAll("%crate%", crate.getCrateName()).replaceAll("%amount%", playerDataManager.getAmount(off, crate) + "").replaceAll("&", "§"));
                                    return true;
                                }
                            }
                            p.sendMessage("§cPlayer not found");
                            return true;
                        }

                    }

                    if(args[0].equalsIgnoreCase("giveall")) {
                        if(!p.hasPermission("virtualcrates.admin")) {
                            p.sendMessage("§cNo Permission.");
                            return true;
                        }
                        Crate crate = CrateAPI.getInstance().getCrateRegistrar().getCrate(args[1]);
                        try {
                            int amount = Integer.parseInt(args[2]);

                            if(amount <= 0) {
                                p.sendMessage(config.getString("messages.conversion_amount_must_be_positive").replaceAll("&", "§"));
                                return true;
                            }

                            if (crate == null) {
                                p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                return true;
                            }

                            if (giveAllCrate(crate, amount)) {
                                p.sendMessage(config.getString("messages.gave_all_key").replaceAll("&", "§").replaceAll("%amount%", amount + "").replaceAll("%crate%", crate.getCrateName()));
                            } else {
                                p.sendMessage(config.getString("messages.key_not_found").replaceAll("&", "§").replaceAll("%crate%", args[1]));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage(config.getString("messages.conversion_amount_must_be_positive").replaceAll("&", "§"));
                            return true;
                        }
                    }
                } else {
                    p.sendMessage("§cNo Permission");
                    return true;
                }
            }
        }

        return true;
    }

    /**
     * @param crate - Crate Obj
     * @param amount - Amount of keys
     * @return true if executed, false if not.
     */
    private boolean giveAllCrate(Crate crate, int amount) {

        for(String item : config.getConfigurationSection("items").getKeys(false)) {

            if(config.getString("items." + item + ".crate_name").equalsIgnoreCase(crate.getCrateName())) {

                for(Player p : Bukkit.getOnlinePlayers()) {
                    playerDataManager.giveCrate(p, crate, amount);

                    String messagePrefix = CrateAPI.getInstance().getConfig().getString("prefix");

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', CrateAPI.getMessage("core.player_given_crate").replaceAll("\\{p\\}", messagePrefix).replaceAll("\\{player\\}", p.getName()).replaceAll("\\{crate\\}", crate.getCrateName()).replaceAll("\\{number\\}", amount+ "")));
                }

                return true;
            }

        }

        return false;
    }


    private final Block getTargetBlock(Player p, int range) {
        BlockIterator iter = new BlockIterator(p, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    }
