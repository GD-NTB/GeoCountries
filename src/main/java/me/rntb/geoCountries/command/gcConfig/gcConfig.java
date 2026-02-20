package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.Map;

public class gcConfig extends SubCommand {

    public gcConfig(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages the plugin config.";
        this.HelpPage   = """
                          §f/gc config [...]: §aManages the plugin config file at config.yml.
                          §f> reload: §2Reloads the config and updates the plugin's state.""";
        this.subSubCommands = Map.ofEntries(
                Map.entry("reload", new gcConfigReload("reload", "/gc config reload", "gc.config"))
        );
    }
}
