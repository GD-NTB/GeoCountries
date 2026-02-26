package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcDebug extends GeoCommand {

    public gcDebug(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Debug commands for development.";
        this.childCommands = new LinkedHashMap<>() {{
            put("createcountry", new gcDebugCreateCountry("createcountry", "/gc debug createcountry", "gc.debug", ItemStack.of(Material.NETHER_STAR)));
            put("soundtest", new gcDebugSoundTest("soundtest", "/gc debug soundtest", "gc.soundtest", ItemStack.of(Material.JUKEBOX)));
        }};
    }
}
