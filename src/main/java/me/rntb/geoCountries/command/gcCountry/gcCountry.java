package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcCountry extends SubCommand {

    public gcCountry(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.HelpPage   = """
                          §f/gc country [...]§a: Manage, edit, and view info about all countries.
                          §f> list: §aLists all countries on the server.
                          §f> info: §2(Case-sensitive) §aDisplays info about a particular country.
                          §f> citizens: §aLists all citizens of a country, their rank, and how many there are.
                          §f> create: §2(Countryless-only) §aCreates a new country.
                          §f> rename: §2(Leader-only) §aRenames your country.
                          §f> dissolve: §2(Leader-only) §aDissolves (deletes) your country.""";
    }

    // todo: make a list of subcommands so we can do autocomplete easier

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aManages, edits, and views info about all countries\n" +
                                                 "Usage: §f/gc country [...]");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc country create
            case "create":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country create");
                    return;
                }
                if (!sender.hasPermission("gc.country.create")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country create", "gc.country.create");
                    return;
                }
                gcCountryCreate.doCommand(sender, subArgs);
                return;

            // gc country dissolve
            case "dissolve":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country dissolve");
                    return;
                }
                if (!sender.hasPermission("gc.country.dissolve")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country dissolve", "gc.country.dissolve");
                    return;
                }
                gcCountryDissolve.onCommand(sender, subArgs);
                return;

            case "rename":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country rename");
                    return;
                }
                if (!sender.hasPermission("gc.country.rename")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country rename", "gc.country.rename");
                    return;
                }
                gcCountryRename.onCommand(sender, subArgs);
                return;

            // gc country list
            case "list":
                if (!sender.hasPermission("gc.country.list")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country list", "gc.country.list");
                    return;
                }
                gcCountryList.onCommand(sender, subArgs);
                return;

            // gc country info
            case "info":
                if (!sender.hasPermission("gc.country.info")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country info", "gc.country.info");
                    return;
                }
                gcCountryInfo.onCommand(sender, subArgs);
                return;

            // gc country citizens
            case "citizens":
                if (!sender.hasPermission("gc.country.citizens")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country citizens", "gc.country.citizens");
                    return;
                }
                gcCountryCitizens.onCommand(sender, subArgs);
                return;

            // gc country [xxx]
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc country§c!\n" +
                                                     "Usage: §f/gc country [...]");
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch(args.length) {
            // /gc country 1
            case 1 -> Stream.of("create", "dissolve", "rename", "list", "info", "citizens").filter(x -> sender.hasPermission("gc.country." + x)).toList();
            // gc country [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc country info [countries]
                    case "info" -> sender.hasPermission("gc.country.info") ? CountryData.AllAsNames(false) : List.of();
                    // /gc country citizens [countries]
                    case "citizens" -> sender.hasPermission("gc.country.citizens") ? CountryData.AllAsNames(false) : List.of();
                    // /gc country [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
