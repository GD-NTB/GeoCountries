package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcConfig extends GeoCommand {

    public gcConfig(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages the plugin's config.";
        this.childCommands = new LinkedHashMap<>() {{
            put("reload", new gcConfigReload("reload", "/gc config reload", "gc.config", ItemStack.of(Material.EMERALD)));
        }};
    }
}
