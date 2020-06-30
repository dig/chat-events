package com.github.dig.chat.event;

import java.util.Random;

public enum EventType {

    COUPON,
    SCRAMBLE;

    private static final Random RANDOM = new Random();
    public static EventType random() {
        return values()[RANDOM.nextInt(values().length)];
    }
}
