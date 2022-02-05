package com.virtualcrates.objects;

import com.virtualcrates.enums.CooldownType;
import org.bukkit.entity.Player;

public class Cooldown {

    private Player player;
    private CooldownType cooldownType;
    public Cooldown(Player player, CooldownType cooldownType) {
        this.player = player;
        this.cooldownType = cooldownType;
    }

    public Player getPlayer() {
        return player;
    }

    public CooldownType getCooldownType() {
        return cooldownType;
    }

}
