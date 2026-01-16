package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcCountry extends SubCommand {

    public gcCountry(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.HelpPage   = """
                          §f/gc country [...]: §aManage, edit, and view info about all countries.
                          §f> list: §aLists all countries on the server.
                          §f> info [country]: §2(Case-sensitive) §aDisplays info about a particular country.
                          §f> citizens [country]: §aLists all citizens of a country, their rank, and how many there are.
                          §f> create [name]: §2(Countryless-only) §aCreates a new country.
                          §f> rename [name]: §2(Leader-only) §aRenames your country.
                          §f> dissolve: §2(Leader-only) §aDissolves (deletes) your country.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }

        String mode = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc country create
            case "create":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.sendPrefixedPlayerOnlyErrorMessage("/gc country create");
                    return;
                }
                if (!sender.hasPermission("gc.country.create")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country create", "gc.country.create");
                    return;
                }
                gcCountryCreate.doCommand(sender, subArgs);
                return;

            // gc country dissolve
            case "dissolve":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.sendPrefixedPlayerOnlyErrorMessage("/gc country dissolve");
                    return;
                }
                if (!sender.hasPermission("gc.country.dissolve")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country dissolve", "gc.country.dissolve");
                    return;
                }
                gcCountryDissolve.onCommand(sender, subArgs);
                return;

            case "rename":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.sendPrefixedPlayerOnlyErrorMessage("/gc country rename");
                    return;
                }
                if (!sender.hasPermission("gc.country.rename")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country rename", "gc.country.rename");
                    return;
                }
                gcCountryRename.onCommand(sender, subArgs);
                return;

            // gc country list
            case "list":
                if (!sender.hasPermission("gc.country.list")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country list", "gc.country.list");
                    return;
                }
                gcCountryList.onCommand(sender, subArgs);
                return;

            // gc country info
            case "info":
                if (!sender.hasPermission("gc.country.info")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country info", "gc.country.info");
                    return;
                }
                gcCountryInfo.onCommand(sender, subArgs);
                return;

            // gc country citizens
            case "citizens":
                if (!sender.hasPermission("gc.country.citizens")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc country citizens", "gc.country.citizens");
                    return;
                }
                gcCountryCitizens.onCommand(sender, subArgs);
                return;

            // gc country [xxx]
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]"""
                                                     .formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch(args.length) {
            // /gc country 1
            case 1 -> Stream.of("create", "dissolve", "rename", "list", "info", "citizens").filter(x -> sender.hasPermission("gc.country." + x)).toList();
            // gc country [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc country info [countries]
                    case "info" -> sender.hasPermission("gc.country.info") ? Country.allAsNames(false) : List.of();
                    // /gc country citizens [countries]
                    case "citizens" -> sender.hasPermission("gc.country.citizens") ? Country.allAsNames(false) : List.of();
                    // /gc country [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
