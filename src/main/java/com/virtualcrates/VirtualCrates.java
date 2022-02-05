package com.virtualcrates;

import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.CratePlugin;
import com.virtualcrates.commands.ConvertCommand;
import com.virtualcrates.commands.VirtualCrateCommand;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.config.MyConfigManager;
import com.virtualcrates.listeners.*;
import com.virtualcrates.managers.*;
import com.virtualcrates.papi.Placeholders;
import com.virtualcrates.util.ItemsUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

public class VirtualCrates extends JavaPlugin {

    private CratePlugin crates;
    private MyConfigManager manager;
    @Getter private MyConfig playerdata;
    @Getter private MyConfig options;
    @Getter private MyConfig cratelocations;
    @Getter private ItemsUtil itemUtil;
    @Getter private PlayerDataManager playerDataManager;
    @Getter private CrateInventoryManager crateInventoryManager;
    @Getter private CrateLocationManager crateLocationManager;
    @Getter private HologramManager hologramManager;
    @Getter private HologramLocationManager hologramLocationManager;
    @Getter private CooldownManager cooldownManager;

    public void onEnable() {

        if(getCratePlugin() == null) {

            Bukkit.getLogger().log(Level.SEVERE, "CrateReloaded not found. Disabling plugin.");
            this.setEnabled(false);
            return;
        }


        manager = new MyConfigManager(this);
        crates = CrateAPI.getInstance();

        setupConfig();
        playerDataManager = new PlayerDataManager(this);
        itemUtil = new ItemsUtil(this);
        crateInventoryManager = new CrateInventoryManager(this);
        crateLocationManager = new CrateLocationManager(this);
        hologramManager = new HologramManager(this);
        hologramLocationManager = new HologramLocationManager(this);
        cooldownManager = new CooldownManager();


        playerDataManager.createPlayerDataFolder();
        playerDataManager.migrateNewData();
        playerDataManager.startSaveTask();
        setupHolograms();
        registerCommands();
        registerListeners();

        if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getLogger().log(Level.WARNING, "[VirtualCrates] PlaceholderAPI not detected! Placeholders will not be registered.");
            return;
        } else {
            new Placeholders(this).register();
        }


    }

    public void onDisable() {
        playerDataManager.saveData();
    }

    private void registerCommands() {
        getCommand("virtualcrates").setExecutor(new VirtualCrateCommand(this));
        getCommand("convert").setExecutor(new ConvertCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CrateInventoryListener(this), this);
        pm.registerEvents(new CrateReceiveListeners(this), this);
        pm.registerEvents(new CrateInteractListeners(this), this);
        pm.registerEvents(new GuiOpenCommandListeners(this), this);
        pm.registerEvents(new PreviewCloseListener(this), this);
        pm.registerEvents(new KeyConversionListener(this), this);
        pm.registerEvents(new PhysicalBulkListener(this), this);
        pm.registerEvents(new PlayerDataListener(this), this);
        if(canHaveNPC()) {
            pm.registerEvents(new NPCCrateInteractListeners(this), this);
        }
    }

    private void setupConfig() {

        cratelocations = manager.getNewConfig("cratelocations.yml");
        cratelocations.saveConfig();

        playerdata = manager.getNewConfig("playerdata.yml");
        //playerdata.saveConfig();

        options = manager.getNewConfig("config.yml");
        options.saveConfig();

        // setting up inventory
        if(!options.contains("inventory")) {
            options.set("inventory.title", "Your Crates");
            options.set("inventory.size", 27);
            options.set("inventory.add_filler_items", true);

            options.set("inventory.filler_item.material", "STAINED_GLASS_PANE");
            options.set("inventory.filler_item.dye_color", "BLACK");
            options.set("inventory.filler_item.name", "&a");

            options.saveConfig();
        }

        if(!options.contains("sounds")) {
            options.set("sounds.open_gui.sound_name", "CHEST_OPEN");
            options.set("sounds.open_gui.volume", 1);
            options.set("sounds.open_gui.pitch", 1);
            options.set("sounds.insufficient_crates.sound_name", "ITEM_BREAK");
            options.set("sounds.insufficient_crates.volume", 1);
            options.set("sounds.insufficient_crates.pitch", 1);
        }
        //setup actions of crates
        if(!options.contains("actions")) {
            options.set("actions.right_click", "PREVIEW_CRATE");
            options.set("actions.left_click", "OPEN_CRATE");
        }

        if(!options.contains("messages")) {
            options.set("messages.insufficient_crates", "&cYou do not have enough %crate% keys.");
            options.set("messages.converted_keys", "&7You have converted &a%amount%&7x %crate% keys.");
            options.set("messages.must_be_sneaking", "&cYou must be sneaking to convert a physical key to a virtual key.");
            options.set("messages.key_not_found", "&cWe could not find the crate key named: %crate%.");
            options.set("messages.key_cant_be_converted", "&cWe cannot convert that key, as it is not in the whitelist.");
            options.set("messages.conversion_amount_must_be_positive", "&cThe number must be greater than 0.");
            options.set("messages.conversion_inventory_full", "&cSince your inventory was full, your keys were dropped beneath you.");
            options.set("messages.must_have_bulk_amount", "&7You must have the minimum bulk amount of &f%amount% &7keys to bulk open.");
            options.set("messages.gave_all_key", "&7You have given everyone &a%amount%x &7%crate% Keys");
            options.set("messages.not_enough_to_convert", "&cYou do not have enough physical keys to convert.");
            options.set("messages.convert_command_syntax", "&c/convert <key> <physical/virtual> <amount> - converts a key to either physical or virtual.");
            options.set("messages.convert_command_syntax_example", "&7&oExample: &f/convert FoodKey physical 10, this would convert 10 virutal FoodKey keys to physical.");
            options.set("messages.reload_config", "&a&lVirtual Crates &7» &fconfig reloaded.");
            options.set("messages.virtual_crate_balance", "&f%player% &7has &f%amount%x &7of &f%crate% &7in their virtual crate balance.");
            options.set("messages.gave_virtualkey", "&7You have successfully added %amount%x %crate% to &f%player%&7s virtual crate balance");
            options.set("messages.received_virtualkey_balance", "&7You have received %amount%x of %crate% virtually");
            options.set("messages.remove_virtualkey", "&7You have successfully removed %amount%x %crate% to &f%player%&7s virtual crate balance");
            options.set("messages.virtual_key_removed", "&f%amount%x &7of %crate% virtually has been removed.");
            options.set("messages.setting_npc", "&aYou are now setting an NPC, right click a npc to set it as a virtual crate. Run command again to cancel.");
            options.set("messages.cancelled_npc_selection", "&cCancelled selecting npc.");
            options.set("messages.set_npc_as_virtualcrate", "&aSuccessfully set clicked npc as a virtual crate.");
            options.set("messages.removed_npc_as_virtualcrate", "&aSuccessfully removed npc as virtual crate.");
            options.set("messages.on_cooldown", "&eYou cannot do this for another &f%delay%&e seconds.");
        }

        ArrayList<String> lore = new ArrayList<String>();

        if(!options.contains("whitelist")) {
            options.set("whitelist.enabled", true, "Whitelist is what keys are allowed to be virtualized.");
            lore.add("FoodKey");
            options.set("whitelist.allowed_keys", lore.toArray());
            lore.clear();
        }


        if(!options.contains("gui_open_commands")) {
            lore.add("crates");
            options.set("gui_open_commands", lore.toArray());
            options.saveConfig();
            options.reloadConfig();
            lore.clear();
        }

        if(!options.contains("holograms")) {
            options.set("holograms.enabled", false);
            options.set("holograms.offset.x", 0.5);
            options.set("holograms.offset.y", 1.0);
            options.set("holograms.offset.z", 0.5);
            options.set("holograms.npc_offset.x", 0.5);
            options.set("holograms.npc_offset.y", 3.0);
            options.set("holograms.npc_offset.z", 0.5);
            lore.add("&aVirtual Crates");
            lore.add("&7Customizable virtual form of Crate Reloaded");
            lore.add("&7Takes the physical crate keys and turns them into virtual keys.");
            lore.add("&7With toggleable hologram support.");
            options.set("holograms.hologram", lore.toArray());
            lore.clear();

        }

        if(!options.contains("key_conversion")) {
            options.set("key_conversion.enabled", true);
            options.set("key_conversion.use_command_only", false);
            options.set("key_conversion.whitelist_enabled", true);
            lore.add("FoodKey");
            options.set("key_conversion.whitelist", lore.toArray());
            lore.clear();
        }

        if(!options.contains("cooldowns")) {
            options.set("cooldowns.bulk_open", 10);
        }

        if(!options.contains("log")) {
            options.set("log.enable_saved_plyerdata_logging", true);
        }

        //setup crate items
        if(!options.contains("items")) {
            options.set("items.crate.material", "TRIPWIRE_HOOK");
            options.set("items.crate.name", "&aFood Crate");
            options.set("items.crate.crate_name", "FoodKey");
            options.set("items.crate.slot", 13);
            options.set("items.crate.data", 0);
            options.set("items.crate.glow", false);
            options.set("items.crate.allow_bulk_open", true);
            options.set("items.crate.require_bulk_amount_to_open", false);
            options.set("items.crate.bulk_amount", 8);
            lore.add("&7This is a crate description");
            lore.add("&7You can add and remove lines as you wish");
            lore.add("&7as well as many crates as you wish.");
            lore.add("&a");
            lore.add("&7Your keys: &f%key_amount%");
            lore.add("&aRight Click &7to preview the crate.");
            lore.add("&aLeft Click &7to open the crate.");
            lore.add("&aShift Click &7to open &a%bulk%&7x at once.");
            options.set("items.crate.lore", lore.toArray());
            lore.clear();
        }

        options.saveConfig();
        options.reloadConfig();

    }

    private void setupHolograms() {

        if(options.getBoolean("holograms.enabled")) {

            if(getHolographicDisplays() != null) {

                hologramManager.createHolograms();

            } else {
                options.set("holograms.enabled", false);
                options.saveConfig();
                Bukkit.getLogger().log(Level.SEVERE, "Unable to enable holograms because Holographic Displays is not installed.");
                return;
            }
        }

    }

    private HolographicDisplays getHolographicDisplays() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");

        if(plugin != null) {
            return HolographicDisplays.getInstance();
        }
        return null;
    }

    private CratePlugin getCratePlugin() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CrateReloaded");

        if(plugin != null) {
            return CrateAPI.getInstance();
        }
        return null;
    }

    public boolean canHaveNPC() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
        return plugin != null;
    }

}
