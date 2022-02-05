package com.virtualcrates.util;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import com.virtualcrates.managers.PlayerDataManager;
import com.hazebyte.crate.api.CrateAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ItemsUtil {

    private VirtualCrates plugin;
    private MyConfig config;
    private PlayerDataManager playerDataManager;

    public ItemsUtil(VirtualCrates plugin) {
        this.plugin = plugin;
        this.config = plugin.getOptions();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    /**
     * @param crateName specified crate to get from the config
     * @return returns the itemstack from the config
     */
    public ItemStack parseCrateItemFromConfig(Player p, String crateName) {

        ArrayList<String> lore = new ArrayList<String>();

        if (!config.contains("items." + crateName)) {
            return null;
        }

        final String cr = config.getString("items." + crateName +".crate_name");

        ItemStack item = new ItemStack(Material.getMaterial(config.getString("items." + crateName + ".material")), 1, (byte) config.getInt("items." + crateName + ".data"));
        if(item.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(config.getString("items." + crateName + ".skull_owner"));
            meta.setDisplayName(config.getString("items." + crateName + ".name").replaceAll("&", "§"));

            if (config.getBoolean("items." + crateName + ".glow")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            for (String string : (ArrayList<String>) config.getList("items." + crateName + ".lore")) {

                if(CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + crateName + ".crate_name")) == null) {
                    System.out.println("[VirtualCrates] Could not find the crate reloaded crate named: " + cr + ".");
                    return new ItemStack(Material.AIR);
                }

                lore.add(string.replaceAll("&", "§").replaceAll("%key_amount%", "" + playerDataManager.getAmount(p, CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + crateName + ".crate_name")))).replaceAll("%bulk%", "" + config.getInt("items." + crateName + ".bulk_amount")));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            lore.clear();
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(config.getString("items." + crateName + ".name").replaceAll("&", "§"));

        if (config.getBoolean("items." + crateName + ".glow")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        for (String string : (ArrayList<String>) config.getList("items." + crateName + ".lore")) {

            if(CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + crateName + ".crate_name")) == null) {
                System.out.println("[VirtualCrates] Could not find the crate reloaded crate named: " + cr + ".");
                return new ItemStack(Material.AIR);
            }

            lore.add(string.replaceAll("&", "§").replaceAll("%key_amount%", "" + playerDataManager.getAmount(p, CrateAPI.getCrateRegistrar().getCrate(config.getString("items." + crateName + ".crate_name")))).replaceAll("%bulk%", "" + config.getInt("items." + crateName + ".bulk_amount")));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();
        return item;
    }

}
