package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcDebug extends GeoCommand {

    public gcDebug(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Debug commands for development.";
        this.childCommands.put("createcountry", new gcDebugCreateCountry(this, "createcountry", "/gc debug createcountry", "gc.debug", ItemStack.of(Material.NETHER_STAR)));
        this.childCommands.put("soundtest", new gcDebugSoundTest(this, "soundtest", "/gc debug soundtest", "gc.debug", ItemStack.of(Material.JUKEBOX)));
    }
}
