package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.Map;

public class gcDebug extends SubCommand {

    public gcDebug(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Debug commands for development.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful debug commands for plugin development.
                          §f> createcountry [name]: §2Creates a test country.
                          §f> soundtest: §2Plays a sound effect.""";
        this.subSubCommands = Map.ofEntries(
                Map.entry("createcountry", new gcDebugCreateCountry("createcountry", "/gc debug createcountry", "gc.debug")),
                Map.entry("soundtest", new gcDebugSoundTest("soundtest", "/gc debug soundtest", "gc.soundtest"))
        );
    }
}
