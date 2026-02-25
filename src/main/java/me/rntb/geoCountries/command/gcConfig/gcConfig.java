package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import java.util.LinkedHashMap;

public class gcConfig extends GeoCommand {

    public gcConfig(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages the plugin config.";
        this.childCommands = new LinkedHashMap<>() {{
            put("reload", new gcConfigReload("reload", "/gc config reload", "gc.config", Material.EMERALD));
        }};
    }
}
