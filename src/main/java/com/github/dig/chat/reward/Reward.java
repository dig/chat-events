package com.github.dig.chat.reward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface Reward {

    void run(ConfigurationSection config, Player player);

}
