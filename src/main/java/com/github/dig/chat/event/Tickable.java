package com.github.dig.chat.event;

public abstract class Tickable {

    protected int tickCount = 0;
    public void tick() {
        tickCount++;
    }
}
