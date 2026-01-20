package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class gcAdmin extends SubCommand {

    public gcAdmin(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Useful admin commands.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful admin commands for managing the server.
                          §f> deletecountry [name]: §2Delete a country from the server.
                          §f> setplayercountry [country] §2Set a player's country.
                          §f> setplayerrank [username] [rank]: §2Set a player's rank.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /gc admin
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
            // gc admin deletecountry
            case "deletecountry":
                gcAdminDeleteCountry.onCommand(sender, subArgs);
                return;

            // gc admin setplayercountry
            case "setplayercountry":
                gcAdminSetPlayerCountry.onCommand(sender, subArgs);
                return;

            // gc admin setplayerrank
            case "setplayerrank":
                gcAdminSetPlayerRank.onCommand(sender, subArgs);
                return;

            // gc admin [xxx]
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
        return switch (args.length) {
            // /gc admin 1
            case 1 -> sender.hasPermission("gc.admin") ? List.of("deletecountry", "setplayercountry", "setplayerrank") : List.of();
            // gc admin [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc admin deletecountry [country]
                    case "deletecountry" -> sender.hasPermission("gc.admin") ? Country.allAsNames(true) : List.of();
                    // /gc admin setplayercountry [players]
                    case "setplayercountry" -> sender.hasPermission("gc.admin") ? PlayerProfile.allAsUsernames(true) : List.of();
                    // /gc admin setplayerrank [players]
                    case "setplayerrank" -> sender.hasPermission("gc.admin") ? PlayerProfile.allAsUsernames(true) : List.of();
                    // /gc admin [...]
                    default -> List.of();
                };
            // gc admin [...] 2
            case 3 ->
                switch (args[0]) {
                    // /gc admin setplayercountry [...] [countries]
                    case "setplayercountry" -> {
                        if (!sender.hasPermission("gc.admin"))
                            yield List.of();
                        List<String> countryNames = new ArrayList<>(Country.allAsNames(true));
                        countryNames.add("null");
                        yield countryNames;
                    }
                    // /gc admin setplayerrank [...] [ranks]
                    case "setplayerrank" -> sender.hasPermission("gc.admin") ? EnumUtil.EnumToStringArray(PlayerProfile.PlayerRank.class) : List.of();
                    // /gc admin [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
