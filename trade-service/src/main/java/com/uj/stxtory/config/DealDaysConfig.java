package com.uj.stxtory.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DealDaysConfig {
    private final AtomicInteger baseDays = new AtomicInteger(130);

    public int getBaseDays() {
        return baseDays.get();
    }

    public int setBaseDays(int baseDays) {
        this.baseDays.set(baseDays);
        return baseDays;
    }
}
