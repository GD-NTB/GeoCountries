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
        this.HelpString = "Purges (deletes) plugin data - should be used very rarely!";
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
