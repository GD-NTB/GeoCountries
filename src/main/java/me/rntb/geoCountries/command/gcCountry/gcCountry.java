package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class gcCountry extends GeoCommand {

    public gcCountry(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages, edits, and views info about all countries.";
        this.childCommands = new LinkedHashMap<>() {{
            put("citizens", new gcCountryCitizens("citizens", "/gc country citizens", "gc.country.citizens", Material.BOOK));
            put("create", new gcCountryCreate("create", "/gc country create", "gc.country.create", Material.NETHER_STAR));
            put("dissolve", new gcCountryDissolve("dissolve", "/gc country dissolve", "gc.country.dissolve", Material.FLINT_AND_STEEL));
            put("info", new gcCountryInfo("info", "/gc country info", "gc.country.info", Material.MAP));
            put("list", new gcCountryList("list", "/gc country list", "gc.country.list", Material.PAPER));
            put("rename", new gcCountryRename("rename", "/gc country rename", "gc.country.rename", Material.NAME_TAG));
            put("settings", new gcCountrySettings("settings", "/gc country settings", "gc.country.settings", Material.WRITABLE_BOOK));
        }};
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("info").onCommandEntered(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }
}
