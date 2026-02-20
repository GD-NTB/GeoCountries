package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcAdmin extends SubCommand {

    public gcAdmin(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Useful admin commands.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful admin commands for managing the server.
                          §f> deletecountry [name]: §2Delete a country from the server.
                          §f> setplayercountry [country] §2Set a player's country.
                          §f> setplayerrank [username] [rank]: §2Set a player's rank.""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("deletecountry", gcAdminDeleteCountry::onCommand),
            Map.entry("setplayercountry", gcAdminSetPlayerCountry::onCommand),
            Map.entry("setplayerrank", gcAdminSetPlayerRank::onCommand)
    );

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
        findAndExecuteSubCommand(sender, args, subCommands, true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc admin [commands]
            case 1 -> subCommands.keySet().stream().toList();

            // gc admin [command] [...]
            case 2 ->
                switch (args[0]) {
                    // /gc admin deletecountry [country]
                    case "deletecountry" -> Country.allAsNames(true);

                    // /gc admin setplayerrank [players]
                    // /gc admin setplayercountry [players]
                    case "setplayerrank", "setplayercountry" -> PlayerProfile.allAsUsernames(true);

                    default -> List.of();
                };

            // gc admin [command] [...] [...]
            case 3 ->
                switch (args[0]) {
                    // /gc admin setplayercountry [...] [countries]
                    case "setplayercountry" -> {
                        List<String> countryNames = new ArrayList<>(Country.allAsNames(true));
                        countryNames.add("null");
                        yield countryNames;
                    }

                    // /gc admin setplayerrank [player] [ranks]
                    case "setplayerrank" -> EnumUtil.enumToStringList(PlayerProfile.PlayerRank.class);

                    default -> List.of();
                };

            default -> List.of();
        };
    }
}
