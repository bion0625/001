package com.uj.stxtory.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DealPageConfig {
    private final AtomicInteger baseDays = new AtomicInteger(130);

    public int getBaseDays() {
        return baseDays.get();
    }

    public void setBaseDays(int baseDays) {
        this.baseDays.set(baseDays);
    }
}
