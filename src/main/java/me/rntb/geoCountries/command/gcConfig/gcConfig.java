package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcConfig extends GeoCommand {

    public gcConfig(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages the plugin's config.";
        this.childCommands.put("reload", new gcConfigReload(this, "reload", "/gc config reload", "gc.config", ItemStack.of(Material.EMERALD)));
    }
}
