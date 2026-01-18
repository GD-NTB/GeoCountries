package me.rntb.geoCountries.command.admin;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class gcAdmin extends SubCommand {

    public gcAdmin(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Useful admin commands.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful admin commands for managing the server.
                          §f> deletecountry [name]: §aDelete a country from the server.
                          §f> setplayerrank [username] [rank]: §aSet a player's rank.""";
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

            // gc admin setplayerrank
            case "setplayerrank":
                gcAdminSetPlayerRank.onCommand(sender, subArgs);
                return;

            // gc admin [xxx]
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]""".formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc admin 1
            case 1 -> sender.hasPermission("gc.admin") ? List.of("deletecountry", "setplayerrank") : List.of();
            // gc admin [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc admin setplayerrank [players]
                    case "setplayerrank" -> sender.hasPermission("gc.admin") ? PlayerProfile.allAsUsernames(true) : List.of();
                    // /gc admin deletecountry [deletecountry]
                    case "deletecountry" -> sender.hasPermission("gc.admin") ? Country.allAsNames(true) : List.of();
                    // /gc admin [...]
                    default -> List.of();
                };
            // gc admin [...] 2
            case 3 ->
                switch (args[0]) {
                    // /gc admin setplayerrank [...] [ranks]
                    case "setplayerrank" -> sender.hasPermission("gc.admin") ? EnumUtil.EnumToStringArray(PlayerProfile.PlayerRank.class) : List.of();
                    // /gc admin [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
