package com.uj.stxtory.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DealDaysConfig {
    private final AtomicInteger stockBaseDays = new AtomicInteger(130);
    private final AtomicInteger upbitBaseDays = new AtomicInteger(130);

    public int getStockBaseDays() {
        return stockBaseDays.get();
    }

    public int getUpbitBaseDays() {
        return upbitBaseDays.get();
    }

    public int setStockBaseDays(int days) {
        this.stockBaseDays.set(days);
        return days;
    }

    public int setUpbitBaseDays(int days) {
        this.upbitBaseDays.set(days);
        return days;
    }
}
