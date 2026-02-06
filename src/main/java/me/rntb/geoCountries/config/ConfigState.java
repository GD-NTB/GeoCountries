package me.rntb.geoCountries.config;

import net.kyori.adventure.text.Component;

public class ConfigState {

    public static final double CONFIG_VERSION = 0.0207; // increment whenever config.yml is changed

    public static boolean debugMode = false;
    public static boolean debugLogging = false;

    public static String chatPrefix = "§8[GeoCountries] ";
    public static Component chatPrefixComponent; // set in configmanager

    public static boolean countryPrefixEnabled = true;
    public static String countryPrefixFormat = "%s[%s]";
    public static int countryPrefixMin = 1;
    public static int countryPrefixMax = 10;

    public static boolean soundEffects = true;

    public static int maxCitizenshipApplications = 10;

    public static int chatResponseMin = 1;
    public static int chatResponseMax = 150;
    public static int countryNameMin = 2;
    public static int countryNameMax = 35;
}
