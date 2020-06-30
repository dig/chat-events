package com.github.dig.chat.event;

import java.util.Random;

public enum EventType {

    SCRAMBLE;

    public static BaseEvent of(EventType type) {
        switch (type) {
            case SCRAMBLE: return new ScrambleEvent();
            default: return null;
        }
    }

    private static final Random RANDOM = new Random();
    public static EventType random() {
        return values()[RANDOM.nextInt(values().length)];
    }
}
