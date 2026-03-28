package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcDebug extends GeoCommand {

    public gcDebug(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Debug commands for development.";
        addChild(new gcDebugCreateCountry("createcountry", "gc.debug", ItemStack.of(Material.NETHER_STAR)));
        addChild(new gcDebugSoundTest("soundtest", "gc.debug", ItemStack.of(Material.JUKEBOX)));
    }
}
