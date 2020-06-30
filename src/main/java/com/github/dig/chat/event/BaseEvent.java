package com.github.dig.chat.event;

import org.bukkit.event.Listener;

public interface BaseEvent extends Listener {

    boolean canRun();
    boolean isRunning();

    void start();
    void stop();

    static BaseEvent of(EventType type) {
        switch (type) {
            case COUPON: return new CouponEvent();
            case SCRAMBLE: return new ScrambleEvent();
            default: return null;
        }
    }
}
