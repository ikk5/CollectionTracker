package com.tracker.collectiontracker.util;

import java.util.Date;

/**
 *
 */
public class TimingUtil {
    private TimingUtil() {
    }

    public static long start() {
        return new Date().getTime();
    }

    public static long duration(long start) {
        return new Date().getTime() - start;
    }
}
