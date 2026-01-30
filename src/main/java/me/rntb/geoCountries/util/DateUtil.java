package me.rntb.geoCountries.util;

public class DateUtil {

    public static long daysAgo(long since) {
        return Math.abs((since - System.currentTimeMillis()) / 86400000);
    }
}
