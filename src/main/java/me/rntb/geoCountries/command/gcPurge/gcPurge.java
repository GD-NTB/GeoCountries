package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcPurge extends SubCommand {

    public gcPurge(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Deletes plugin data.";
        this.HelpPage   = """
                          §f/gc purge [...]: §aPurges (deletes) specific data within the plugin's persistent storage, such as data collections, etc.
                          §cShould be used very very rarely!
                          §f> citizenshipapplication: §2Purges all citizenship applications in memory.
                          §f> country: §2Purges all Country data collections.
                          §f> playerprofile: §2Purges all PlayerProfile data collections.
                          §f> uuid [uuid]: §2Purges a PlayerProfile by UUID.;
                          §f> username [username]: §2Purges a PlayerProfile by username.""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("citizenshipapplication", gcPurgeCitizenshipApplication::onCommand),
            Map.entry("country", gcPurgeCountry::onCommand),
            Map.entry("playerprofile", gcPurgePlayerProfile::onCommand),
            Map.entry("uuid", gcPurgeUUID::onCommand),
            Map.entry("username", gcPurgeUsername::onCommand)
    );

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }
        findAndExecuteSubCommand(sender, args, subCommands);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc purge [commands]
            case 1 -> subCommands.keySet().stream().toList();

            // /gc purge [...] 2
            case 2 ->
                switch (args[0]) {
                    case "username" -> PlayerProfile.allAsUsernames(true);
                    case "uuid" -> PlayerProfile.allAsUUIDStrings();
                    default -> List.of();
                };

            default -> List.of();
        };
    }
}
