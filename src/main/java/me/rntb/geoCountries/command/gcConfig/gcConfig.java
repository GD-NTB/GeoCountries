package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcConfig extends GeoCommand {

    public gcConfig(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Manages the plugin's config.");
        addChild(new gcConfigReload("reload", "gc.config", ItemStack.of(Material.EMERALD)));
    }
}
