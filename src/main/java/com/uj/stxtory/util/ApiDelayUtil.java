package com.uj.stxtory.util;

public class ApiDelayUtil {
    public static void setDelay(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
