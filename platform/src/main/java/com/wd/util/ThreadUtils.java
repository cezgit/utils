package com.wd.util;

public class ThreadUtils {

    public static final void sleep(long forMillis) {
        try {
            Thread.sleep(forMillis);
        } catch (InterruptedException iex) {
            throw new RuntimeException("sleep was interrupted", iex);
        }
    }
}
