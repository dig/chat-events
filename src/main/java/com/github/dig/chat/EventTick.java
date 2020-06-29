package com.github.dig.chat;

import com.github.dig.chat.event.BaseEvent;
import com.github.dig.chat.event.Tickable;

public class EventTick implements Runnable {

    private final ChatEvents chatEvents = ChatEvents.getInstance();

    @Override
    public void run() {
        BaseEvent event = chatEvents.getEvent();
        if (event != null && event instanceof Tickable) {
            Tickable tickable = (Tickable) event;
            tickable.tick();
        }
    }
}
