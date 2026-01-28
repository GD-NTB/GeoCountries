package me.rntb.geoCountries.config;

import net.kyori.adventure.text.Component;

public class ConfigState {

    public static boolean DebugMode = false;
    public static boolean DebugLogging = false;

    public static String ChatPrefix = "§8[GeoCountries] ";
    public static Component ChatPrefixComponent;

    public static boolean SoundEffects = true;

    public static int MaxCitizenshipApplications = 5;

    public static int ChatResponseMin = 1;
    public static int ChatResponseMax = 150;
    public static int CountryNameMin = 2;
    public static int CountryNameMax = 35;
}
