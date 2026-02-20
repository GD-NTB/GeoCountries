package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class gcCountry extends SubCommand {

    public gcCountry(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.HelpPage   = """
                          §f/gc country [...]: §aManage, edit, and view info about all countries.
                          §f> citizens [country?]: §2Lists all citizens of your/any country, their rank, and how many.
                          §f> create [name]: §2Creates a new country.
                          §f> dissolve: §2Dissolves (deletes) your country.;
                          §f> info [country?]: §2Displays info about your/any particular country.
                          §f> list: §2Lists all countries on the server.
                          §f> rename [name]: §2Renames your country.
                          §f> settings [setting?] [value?]: §2Sets/lists your country's settings""";
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
            // do /gc country info
            if (!sender.hasPermission("gc.country.info")) {
                ChatUtil.sendNoPermissionMessage(sender, "/gc country info", "gc.country.info");
                return;
            }
            this.subSubCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, false);
    }
}
