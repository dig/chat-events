package com.github.dig.chat.event;

import org.bukkit.event.Listener;

public interface BaseEvent extends Listener {

    boolean canRun();
    boolean isRunning();

    void start();
    void stop();

}
