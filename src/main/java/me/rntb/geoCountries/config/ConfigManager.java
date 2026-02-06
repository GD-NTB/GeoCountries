package me.rntb.geoCountries.config;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

// when editing config options, update readStateFromFile and writeStateToFile methods
// remember to update config-version and update check in init!
public class ConfigManager {

    private static FileConfiguration config;
    private static final LegacyComponentSerializer legacySerialisation = LegacyComponentSerializer.legacySection();

    public static void init() {
        GeoCountries.self.saveResource("config.yml", false); // create from resources/config.yml if not exist
        config = GeoCountries.self.getConfig();

        ChatUtil.sendPrefixedLogMessage(String.valueOf(config.getDouble("config-version", 0)));
        if (config.getDouble("config-version", 0) != ConfigState.CONFIG_VERSION)
            updateConfig();

        readStateFromFile();

        ChatUtil.sendPrefixedLogMessage("Read plugin.yml!");
    }

    private static void updateConfig() {
        // read old values
        readStateFromFile();

        // overwrite disk config with new config.yml
        GeoCountries.self.saveResource("config.yml", true);

        // set memory config to new config.yml
        GeoCountries.self.reloadConfig();
        config = GeoCountries.self.getConfig();

        // write old values to memory config
        writeStateToFile();

        // write memory config to disk config
        GeoCountries.self.saveConfig();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Updated config.yml!");
    }

    public static void reload() {
        GeoCountries.self.reloadConfig();
        config = GeoCountries.self.getConfig();

        readStateFromFile();

        ChatUtil.sendPrefixedLogMessage("Config reloaded!");
    }

    private static void readStateFromFile() {
        ConfigState.debugMode = config.getBoolean("debug-mode");
        ConfigState.debugLogging = config.getBoolean("debug-logging");

        ConfigState.chatPrefix = config.getString("chat-prefix") + "§r";
        ConfigState.chatPrefixComponent = legacySerialisation.deserialize(ConfigState.chatPrefix);

        ConfigState.countryPrefixEnabled = config.getBoolean("country-prefix-enabled");
        ConfigState.countryPrefixFormat = config.getString("country-prefix-format") + "§r";
        ConfigState.countryPrefixMin = config.getInt("country-prefix-min");
        ConfigState.countryPrefixMax = config.getInt("country-prefix-max");

        ConfigState.soundEffects = config.getBoolean("sound-effects");

        ConfigState.maxCitizenshipApplications = config.getInt("max-citizenship-applications");

        ConfigState.chatResponseMin = config.getInt("chat-response-min");
        ConfigState.chatResponseMax = config.getInt("chat-response-max");
        ConfigState.countryNameMin = config.getInt("country-name-min");
        ConfigState.countryNameMax = config.getInt("country-name-max");
    }

    private static void writeStateToFile() {
        config.set("debug-mode", ConfigState.debugMode);
        config.set("debug-logging", ConfigState.debugLogging);

        config.set("chat-prefix", ConfigState.chatPrefix.replace("§r", ""));

        config.set("country-prefix-enabled", ConfigState.countryPrefixEnabled);
        config.set("country-prefix-format", ConfigState.countryPrefixFormat.replace("§r", ""));
        config.set("country-prefix-min", ConfigState.countryPrefixMin);
        config.set("country-prefix-max", ConfigState.countryPrefixMax);

        config.set("sound-effects", ConfigState.soundEffects);

        config.set("max-citizenship-applications", ConfigState.maxCitizenshipApplications);

        config.set("chat-response-min", ConfigState.chatResponseMin);
        config.set("chat-response-max", ConfigState.chatResponseMax);
        config.set("country-name-min", ConfigState.countryNameMin);
        config.set("country-name-max", ConfigState.countryNameMax);
    }
}
