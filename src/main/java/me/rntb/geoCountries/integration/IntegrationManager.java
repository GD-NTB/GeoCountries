package me.rntb.geoCountries.integration;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;

public class IntegrationManager {

    // called in GeoCountries.onEnable()
    public static void init() {
        detectPlugins();

        Pl3xMapIntegration.init();
    }

    private static void detectPlugins() {
        if (GeoCountries.pluginManager.isPluginEnabled("Pl3xMap"))
            IntegrationState.isPl3xMapEnabled = true;
    }

    public static void disable() {
        clearAll();

        IntegrationState.isPl3xMapEnabled = false;
    }

    public static void onClaim(Country country, int x, int z) {
        Pl3xMapIntegration.onClaim(country, x, z);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("IntegrationManager.onClaim");
    }

    public static void onUnclaim(Country country, int x, int z) {
        Pl3xMapIntegration.onUnclaim(country, x, z);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("IntegrationManager.onUnclaim");
    }

    public static void onStyleUpdate(Country country) {
        Pl3xMapIntegration.onStyleUpdate(country);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("IntegrationManager.onStyleUpdate");
    }

    public static void clearCountry(Country country) {
        Pl3xMapIntegration.clearCountry(country);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("IntegrationManager.clearCountry");
    }

    public static void clearAll() {
        Pl3xMapIntegration.clearAll();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("IntegrationManager.clearAll");
    }
}
