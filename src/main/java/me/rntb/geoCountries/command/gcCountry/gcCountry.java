package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class gcCountry extends SubCommand {

    public gcCountry(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.subSubCommands = Map.ofEntries(
                Map.entry("citizens", new gcCountryCitizens("citizens", "/gc country citizens", "gc.country.citizens")),
                Map.entry("create", new gcCountryCreate("create", "/gc country create", "gc.country.create")),
                Map.entry("dissolve", new gcCountryDissolve("dissolve", "/gc country dissolve", "gc.country.dissolve")),
                Map.entry("info", new gcCountryInfo("info", "/gc country info", "gc.country.info")),
                Map.entry("list", new gcCountryList("list", "/gc country list", "gc.country.list")),
                Map.entry("rename", new gcCountryRename("rename", "/gc country rename", "gc.country.rename")),
                Map.entry("settings", new gcCountrySettings("settings", "/gc country settings", "gc.country.settings"))
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            subSubCommands.get("info").onCommandEntered(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, false);
    }
}
