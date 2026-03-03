package me.rntb.geoCountries.integration;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;

public class IntegrationManager {

    // called in GeoCountries.onEnable()
    public static void init() {
        detectPlugins();

        if (ConfigState.enablePl3xMap)
            Pl3xMapIntegration.init();
    }

    private static void detectPlugins() {
        if (GeoCountries.pluginManager.isPluginEnabled("Pl3xMap"))
            IntegrationState.isPl3xMapEnabled = true;

    }
}
