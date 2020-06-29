package com.github.dig.chat;

import com.github.dig.chat.event.BaseEvent;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.logging.Level;

import static com.sun.org.apache.xalan.internal.utils.SecuritySupport.getResourceAsStream;

@Log
public class ChatEvents extends JavaPlugin {

    @Getter
    private static ChatEvents instance;
    private static String[] defaultFiles = new String[]{"config.yml", "scramble.txt"};

    @Getter
    private BaseEvent event = null;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultFiles(defaultFiles);

        FileConfiguration fileConfiguration = getConfig();
        long tickTime = fileConfiguration.getInt("event-timer-seconds", 1800) * 20;

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, new EventSelectionTask(), tickTime, tickTime);
        scheduler.runTaskTimer(this, new EventTick(), 1L, 1L);
    }

    public void setEvent(BaseEvent event) {
        if (this.event != null) {
            HandlerList.unregisterAll(this.event);
            this.event.stop();
        }
        this.event = event;
        getServer().getPluginManager().registerEvents(this.event, this);
        event.start();
    }

    private void saveDefaultFiles(String[] fileNames) {
        for (String fileName : fileNames) {
            try {
                File file = new File(getDataFolder(), fileName);
                if (!file.exists()) {
                    getDataFolder().mkdirs();
                    file.createNewFile();

                    try (InputStream is = getResourceAsStream(fileName);
                         OutputStream os = new FileOutputStream(file)) {
                        ByteStreams.copy(is, os);
                        log.log(Level.INFO, "Saved " + file.getName());
                    }
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, "Unable to copy resource", e);
            }
        }
    }
}
