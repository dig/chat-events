package com.github.dig.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            String subCommand = args[0];
            if (subCommand.equalsIgnoreCase("reload") && sender.hasPermission("chatevents.reload")) {
                ChatEvents.getInstance().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded config.");
            }
        }
        return false;
    }
}
