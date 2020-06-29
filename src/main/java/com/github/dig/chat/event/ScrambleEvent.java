package com.github.dig.chat.event;

import com.github.dig.chat.ChatEvents;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

@Log
public class ScrambleEvent implements BaseEvent {

    private static final String SCRAMBLE_KEY = "scramble";
    private static final Random RANDOM = new Random();

    private ChatEvents chatEvents = ChatEvents.getInstance();
    private ConfigurationSection config = chatEvents.getConfig().getConfigurationSection("events." + SCRAMBLE_KEY);
    private String wordListName = config.getString("word-list", "scramble.txt");

    private Optional<UUID> winnerUUID;
    private String word;

    @Override
    public boolean canRun() {
        File wordList = new File(chatEvents.getDataFolder(), wordListName);
        return config.getBoolean("enabled") && wordList.exists();
    }

    @Override
    public boolean isRunning() {
        return !winnerUUID.isPresent();
    }

    @Override
    public void start() {
        winnerUUID = Optional.empty();

        File wordList = new File(chatEvents.getDataFolder(), wordListName);
        try {
            List<String> words = Files.readAllLines(wordList.toPath());
            word = words.get(RANDOM.nextInt(words.size()));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to read word list for Scramble.", e);
        }
    }

    @Override
    public void stop() {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(word) && !winnerUUID.isPresent()) {
            winnerUUID = Optional.ofNullable(event.getPlayer().getUniqueId());
        }
    }
}
