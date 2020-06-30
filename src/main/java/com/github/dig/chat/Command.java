package com.github.dig.chat;

import com.github.dig.chat.event.BaseEvent;
import com.github.dig.chat.event.EventType;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.logging.Level;

@Log
public class Command implements CommandExecutor {

    private ChatEvents chatEvents = ChatEvents.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            String subCommand = args[0];
            if (subCommand.equalsIgnoreCase("reload") && sender.hasPermission("chatevents.reload")) {
                chatEvents.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded config.");
            }
        } else if (args.length == 2) {
            String subCommand = args[0];
            if (subCommand.equalsIgnoreCase("force") && sender.hasPermission("chatevents.force")) {
                try {
                    EventType eventType = EventType.valueOf(args[1].toUpperCase());
                    chatEvents.setEvent(BaseEvent.of(eventType));
                    sender.sendMessage(ChatColor.GREEN + "Event forced.");
                } catch (IllegalArgumentException | NullPointerException e) {
                    log.log(Level.SEVERE, "Unable to start event", e);
                    sender.sendMessage(ChatColor.RED + "That event doesn't exist!");
                }
            }
        } else {
            Arrays.asList(
                    ChatColor.RED + "Commands:",
                    ChatColor.RED + "/chatevents reload - Reload config",
                    ChatColor.RED + "/chatevents force <type> - Starts event"
            ).forEach(s -> sender.sendMessage(s));
        }
        return true;
    }
}
