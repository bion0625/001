package com.uj.stxtory.util;

public class ApiUtil {
    public static void setDelay(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void runWithException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
