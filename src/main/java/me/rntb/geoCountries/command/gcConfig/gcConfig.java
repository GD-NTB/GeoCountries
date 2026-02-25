package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcConfig extends SubCommand {

    public gcConfig(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages the plugin config.";
        this.subSubCommands = new LinkedHashMap<>() {{
            put("reload", new gcConfigReload("reload", "/gc config reload", "gc.config"));
        }};
    }
}
