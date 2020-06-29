package com.github.dig.chat.event;

import com.github.dig.chat.ChatEvents;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

@Log
public class ScrambleEvent implements BaseEvent {

    private static final String SCRAMBLE_KEY = "scramble";
    private static final Random RANDOM = new Random();

    private final ChatEvents chatEvents = ChatEvents.getInstance();
    private final ConfigurationSection config = chatEvents.getConfig().getConfigurationSection("events." + SCRAMBLE_KEY);
    private final ConfigurationSection msgConfig = chatEvents.getConfig().getConfigurationSection("messages." + SCRAMBLE_KEY);
    private final String wordListName = config.getString("word-list", "scramble.txt");

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

            String shuffledWord = shuffle(word);
            String announce = ChatColor.translateAlternateColorCodes('&', String.format(msgConfig.getString("announce", ""), shuffledWord));
            Bukkit.broadcastMessage(announce);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to read word list for Scramble.", e);
        }
    }

    @Override
    public void stop() {
    }

    private String shuffle(String value) {
        List<Character> chars = new ArrayList<Character>(word.length());
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        char[] shuffled = new char[chars.size()];
        for (int i = 0; i < shuffled.length; i++) {
            shuffled[i] = chars.get(i);
        }
        String shuffledWord = new String(shuffled);
        return shuffledWord;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals(word) && !winnerUUID.isPresent()) {
            winnerUUID = Optional.ofNullable(player.getUniqueId());

            String winner = ChatColor.translateAlternateColorCodes('&', String.format(msgConfig.getString("winner", ""), player.getName(), word));
            Bukkit.broadcastMessage(winner);

            event.setCancelled(true);
        }
    }
}
