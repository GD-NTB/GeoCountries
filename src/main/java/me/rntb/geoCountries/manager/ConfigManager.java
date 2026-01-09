package me.rntb.geoCountries.manager;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void Init() {
        GeoCountries.self.saveResource("config.yml", false); // create from resources/config.yml if not exist
        config = GeoCountries.self.getConfig();

        ReadFromFile();
    }

    public static void Reload() {
        GeoCountries.self.reloadConfig();
        config = GeoCountries.self.getConfig();

        ReadFromFile();
    }

    private static void ReadFromFile() {
        ChatUtil.ChatPrefix = config.getString("chat-prefix") + "§r";

        ChatUtil.SendPrefixedLogMessage("Config loaded from plugin.yml!");
    }
}
