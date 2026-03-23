package me.rntb.geoCountries.config;

import net.kyori.adventure.text.Component;

public class ConfigState {

    public static final double CONFIG_VERSION = 0.025; // increment whenever config.yml is changed

    // claiming
    public static String claimWorld = "world";

    // map plugins
    public static boolean enablePl3xMap = true;

    // commands
    public static boolean enableGcConfirm = true;

    // chat
    public static boolean countryPrefixEnabled = true;
    public static String chatPrefix = "§8[GeoCountries] ";
    public static Component chatPrefixComponent; // set in configmanager
    public static String countryPrefixFormat = "%s[%s]";

    // sound
    public static boolean soundEffects = true;

    // invites
    public static int maxCitizenshipApplications = 10;
    public static int maxFactionInvites = 10;

    // string lengths
    public static int chatResponseMin = 1;
    public static int chatResponseMax = 150;
    public static int countryNameMin = 2;
    public static int countryNameMax = 40;
    public static int countryMottoMin = 1;
    public static int countryMottoMax = 50;
    public static int countryPrefixMin = 1;
    public static int countryPrefixMax = 10;
    public static int factionNameMin = 2;
    public static int factionNameMax = 40;

    // debug
    public static boolean debugMode = false;
    public static boolean debugLogging = false;
}
