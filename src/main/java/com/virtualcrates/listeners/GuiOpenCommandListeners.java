package com.virtualcrates.listeners;

import com.virtualcrates.VirtualCrates;
import com.virtualcrates.config.MyConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class GuiOpenCommandListeners implements Listener {

    private VirtualCrates plugin;
    private MyConfig config;
    public GuiOpenCommandListeners(VirtualCrates instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
    }

    @EventHandler
    public void onPlayerRunCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();
        if(!e.getMessage().startsWith("/")) {
            return;
        }

        if(!config.contains("gui_open_commands")) {
            return;
        }

        ArrayList<String> commands = (ArrayList<String>) config.getList("gui_open_commands");

        String command = e.getMessage().substring(1);

        if(args(command).length >= 2) {
            return;
        }

        for(String cmd : commands) {
            if(cmd.equalsIgnoreCase(command)) {
                e.setCancelled(true);
                p.performCommand("virtualcrates open");
                return;
            }
        }
    }

    private String[] args(String command) {
        String[] args = command.split(" ");
        return args;
    }


}
