package com.github.dig.chat;

import lombok.NonNull;
import org.bukkit.ChatColor;

public class MessageParser {

    public static String parse(@NonNull String message) {
        // TODO: add minimessage support
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
