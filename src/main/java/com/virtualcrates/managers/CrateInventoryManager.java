package com.virtualcrates.managers;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.util.ItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateInventoryManager {

    private VirtualCrates plugin;
    private PlayerDataManager playerdata;
    private ItemsUtil itemUtil;
    private MyConfig config;
    public CrateInventoryManager(VirtualCrates instance) {
        plugin = instance;
        config = plugin.getOptions();
        playerdata = plugin.getPlayerDataManager();
        itemUtil = plugin.getItemUtil();
    }

    public void addItemsToInventory(Player p, Inventory inv) {
        for(int i = 0; i < inv.getSize(); i++) {
            ItemStack filler = new ItemStack(Material.getMaterial(config.getString("inventory.filler_item.material")), 1, (byte) DyeColor.valueOf(config.getString("inventory.filler_item.dye_color")).ordinal());
            ItemMeta meta = filler.getItemMeta();
            meta.setDisplayName(config.getString("inventory.filler_item.name").replaceAll("&", "§"));
            filler.setItemMeta(meta);

            inv.setItem(i, filler);
        }

        for(String crates : config.getConfigurationSection("items").getKeys(false)) {
            if(itemUtil.parseCrateItemFromConfig(p, crates) == null) {
                continue;
            }

            if(itemUtil.parseCrateItemFromConfig(p, crates) != null) {
                inv.setItem(config.getInt("items." + crates + ".slot"), itemUtil.parseCrateItemFromConfig(p, crates));
            }
        }

    }

    public Inventory getVirtualCrates(Player p) {

        Inventory inv = Bukkit.createInventory(p, config.getInt("inventory.size"), config.getString("inventory.title").replaceAll("&", "§"));

        addItemsToInventory(p, inv);

        return inv;
    }
}
