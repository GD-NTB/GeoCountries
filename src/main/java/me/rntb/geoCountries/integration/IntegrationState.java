package me.rntb.geoCountries.integration;

public class IntegrationState {

    public static boolean isPl3xMapEnabled = false;

    public static boolean isAnyMapPluginEnabled() {
        return isPl3xMapEnabled || true; // ...
    }
}
