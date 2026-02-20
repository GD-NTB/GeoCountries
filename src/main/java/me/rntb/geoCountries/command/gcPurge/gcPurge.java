package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

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
                          §f> uuid [uuid]: §2Purges a PlayerProfile by UUID.
                          §f> username [username]: §2Purges a PlayerProfile by username.""";
        this.subSubCommands = Map.ofEntries(
                Map.entry("citizenshipapplication", new gcPurgeCitizenshipApplication("citizenshipapplication", "/gc purge citizenshipapplication", "gc.purge")),
                Map.entry("country", new gcPurgeCountry("country", "/gc purge country", "gc.purge")),
                Map.entry("playerprofile", new gcPurgePlayerProfile("playerprofile", "/gc purge playerprofile", "gc.purge")),
                Map.entry("uuid", new gcPurgeUUID("uuid", "/gc purge uuid", "gc.purge")),
                Map.entry("username", new gcPurgeUsername("username", "/gc purge username", "gc.purge"))
        );
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc purge [commands]
            case 1 -> subSubCommands.keySet().stream().toList();

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
