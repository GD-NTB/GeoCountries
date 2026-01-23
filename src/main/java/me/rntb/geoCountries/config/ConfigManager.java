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

        if (config.getDouble("config-version", 0) < 0.201)
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

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Updated config.yml!");
    }

    public static void reload() {
        GeoCountries.self.reloadConfig();
        config = GeoCountries.self.getConfig();

        readStateFromFile();

        ChatUtil.sendPrefixedLogMessage("Config reloaded!");
    }

    private static void readStateFromFile() {
        ConfigState.DebugLogging = config.getBoolean("debug-logging");
        ConfigState.ChatPrefix = config.getString("chat-prefix") + "§r";
        ConfigState.ChatPrefixComponent = legacySerialisation.deserialize(ConfigState.ChatPrefix);
        ConfigState.SoundEffects = config.getBoolean("sound-effects");
        ConfigState.ChatResponseMin = config.getInt("chat-response-min");
        ConfigState.ChatResponseMax = config.getInt("chat-response-max");
        ConfigState.CountryNameMin = config.getInt("country-name-min");
        ConfigState.CountryNameMax = config.getInt("country-name-max");
    }

    private static void writeStateToFile() {
        config.set("debug-logging", ConfigState.DebugLogging);
        config.set("chat-prefix", ConfigState.ChatPrefix.replace("§r", ""));
        config.set("sound-effects", ConfigState.SoundEffects);
        config.set("chat-response-min", ConfigState.ChatResponseMin);
        config.set("chat-response-max", ConfigState.ChatResponseMax);
        config.set("country-name-min", ConfigState.CountryNameMin);
        config.set("country-name-max", ConfigState.CountryNameMax);
    }
}
