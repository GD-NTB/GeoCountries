package me.rntb.geoCountries.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {

    public static long daysAgo(long value) {
        return Math.abs((value - System.currentTimeMillis()) / 86400000);
    }

    public static String converTimeToString(long value) {
        Instant instant = Instant.ofEpochMilli(value);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }
}
