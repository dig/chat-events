package com.github.dig.chat.event;

import com.github.dig.chat.ChatEvents;
import com.github.dig.chat.MessageParser;
import com.github.dig.chat.reward.TebexCouponReward;
import lombok.NonNull;
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

            String shuffledWord = shuffle();
            String announceMsg = msgConfig.getString("announce", "")
                    .replaceAll("%word", shuffledWord);
            Bukkit.broadcastMessage(MessageParser.parse(announceMsg));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to read word list for Scramble.", e);
        }
    }

    @Override
    public void stop() {
    }

    private String shuffle() {
        List<Character> chars = new ArrayList<>(word.length());
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
            Bukkit.getScheduler().runTask(chatEvents, () -> win(player));
            event.setCancelled(true);
        }
    }

    private void win(@NonNull Player player) {
        winnerUUID = Optional.ofNullable(player.getUniqueId());

        String winnerMsg = msgConfig.getString("winner")
                .replaceAll("%player", player.getName())
                .replaceAll("%word", word);
        Bukkit.broadcastMessage(MessageParser.parse(winnerMsg));

        String rewardMsg = msgConfig.getString("reward");
        player.sendMessage(MessageParser.parse(rewardMsg));

        if (config.contains("reward.commands")) {
            config.getStringList("reward.commands").stream()
                    .map(s -> s.replaceAll("%player", player.getName()))
                    .forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s));
        }
    }
}
