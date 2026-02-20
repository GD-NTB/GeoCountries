package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class gcAdmin extends SubCommand {

    public gcAdmin(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Useful admin commands.";
        this.HelpPage = """
                §f/gc debug [...]: §aUseful admin commands for managing the server.
                §f> deletecountry [name]: §2Delete a country from the server.
                §f> setplayercountry [country] §2Set a player's country.
                §f> setplayerrank [username] [rank]: §2Set a player's rank.""";
        this.subSubCommands = Map.ofEntries(
                Map.entry("deletecountry", new gcAdminDeleteCountry("deletecountry", "/gc admin deletecountry", "gc.admin")),
                Map.entry("setplayercountry", new gcAdminSetPlayerCountry("setplayercountry", "/gc admin setplayercountry", "gc.admin")),
                Map.entry("setplayerrank", new gcAdminSetPlayerRank("setplayerrank", "/gc admin setplayerrank", "gc.admin"))
        );
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return this.subSubCommands.keySet().stream().sorted().toList();
    }
}
