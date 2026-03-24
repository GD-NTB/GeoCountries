package me.rntb.geoCountries.util;

import java.time.format.DateTimeFormatter;

public class StringUtil {

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String leadingS(long count) {
        return count == 1 ? "" : "s";
    }

    public static String sentenceCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String appendTrailingResetFormatter(String s) {
        return s != null && s.endsWith("§r") ? s : s + "§r";
    }

    public static String stripTrailingResetFormatter(String s) {
        return s != null && s.endsWith("§r") ? s.substring(0, s.length() - 2) : s;
    }

    private static final String ARROWS = "↑→↓←";
    public static char yawAngleToArrow(double angle) {
        double degrees = (angle + 225) % 360;
        if (degrees < 0) degrees += 360;
        int i = (int) Math.floor(degrees / 90);
        return ARROWS.charAt(i % 4);
    }
}
