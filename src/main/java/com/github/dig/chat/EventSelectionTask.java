package com.github.dig.chat;

import com.github.dig.chat.event.BaseEvent;
import com.github.dig.chat.event.EventType;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
public class EventSelectionTask implements Runnable {

    private final ChatEvents chatEvents = ChatEvents.getInstance();

    @Override
    public void run() {
        BaseEvent currentEvent = chatEvents.getEvent();
        if (currentEvent == null || !currentEvent.isRunning()) {
            int i = 0;
            BaseEvent next = null;
            while ((next == null || !next.canRun()) && i < EventType.values().length) {
                next = EventType.of(EventType.random());
                i++;
            }

            if (next != null && next.canRun()) {
                log.log(Level.INFO, "Started new event.");
                chatEvents.setEvent(next);
            } else {
                log.log(Level.SEVERE, "Unable to start new event because non are setup correctly.");
            }
        }
    }
}
