package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class gcPurge extends SubCommand {

    public gcPurge(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Purges (deletes) plugin data.";
        this.HelpPage   = """
                          §f/gc purge [...]: §aPurges (deletes) specific data within the plugin's persistent storage, such as data collections, etc.
                          §cShould be used very very rarely!
                          §f> playerprofile: §aPurges all PlayerProfile data collections.
                          §f> country: §aPurges all Country data collections.
                          §f> username [username]: §aPurges a PlayerProfile by username.
                          §f> uuid [uuid]: §aPurges a PlayerProfile by UUID.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc purge
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]""".formatted(this.HelpString, this.DisplayName));
            return;
        }

        String mode = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // /gc purge username
            case "username":
                gcPurgeUsername.doCommand(sender, subArgs);
                return;

            // /gc purge uuid
            case "uuid":
                gcPurgeUUID.doCommand(sender, subArgs);
                return;

            // /gc purge playerprofile
            case "playerprofile":
                gcPurgePlayerProfile.doCommand(sender, subArgs);
                return;

            //gc purge country
            case "country":
                gcPurgeCountry.doCommand(sender, subArgs);
                return;

            //gc purge citizenshipapplication
            case "citizenshipapplication":
                gcPurgeCitizenshipApplication.doCommand(sender, subArgs);
                return;

            // gc purge [xxx]
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
            // /gc purge 1
            case 1 -> sender.hasPermission("gc.purge") ? List.of("username", "uuid", "playerprofile", "country", "citizenshipapplication") : List.of();
            // /gc purge [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc purge username [usernames]
                    case "username" -> sender.hasPermission("gc.purge") ? PlayerProfile.allAsUsernames(true) : List.of();
                    // /gc purge uuid [uuids]
                    case "uuid" -> sender.hasPermission("gc.purge") ? PlayerProfile.allAsUUIDStrings() : List.of();
                    // /gc purge [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
