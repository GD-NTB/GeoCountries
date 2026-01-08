package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class gcPurgeCommand extends SubCommand {

    public gcPurgeCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Purge all references in all data collections about a player. Should be used very rarely.";
        this.HelpPage   = "§f/gc purge [...]§a: Purge all references in all data collections about a player.§r";
    }

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc purge
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aPurges all references in all data collections about a player. Should be used very rarely.§r");
            return;
        }

        String mode = args[0];

        // /gc purge [username/id/playerdata/countrydata]
        if (args.length == 1) {
            // /gc purge username
            if (mode.equalsIgnoreCase("username")) {
                ChatUtil.SendPrefixedMessage(sender, "§cYou need to specify the username of the player as it appears in the data collections.\n" +
                                                     "Usage: §f/gc purge username [...]§r");
                return;
            }

            // /gc purge uuid
            else if (mode.equalsIgnoreCase("uuid")) {
                ChatUtil.SendPrefixedMessage(sender, "§cYou need to specify the UUID of the player as it appears in the data collections.\n" +
                                                     "Usage: §f/gc purge uuid [...]§r");
                return;
            }

            // /gc purge playerdata
            else if (mode.equalsIgnoreCase("playerdata")) {
                int allLength = PlayerData.All.toArray().length;
                for (PlayerData pd : new ArrayList<>(PlayerData.All)) { // new ArrayList as we are concurrently modifying
                    PlayerData.Delete(pd);
                }
                ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a PlayerDatas.§r");
                return;
            }

            //gc purge countrydata
            else if (mode.equalsIgnoreCase("countrydata")) {
                int allLength = CountryData.All.toArray().length;
                for (CountryData cd : new ArrayList<>(CountryData.All)) { // new ArrayList as we are concurrently modifying
                    CountryData.Delete(cd);
                }
                ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a CountryDatas.§r");
                return;
            }

            // gc [xxx]
            else {
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/purge§c!\n" +
                                                     "Usage: §f/gc purge [username/uuid] [...]§r");
                return;
            }
        }

        // /gc purge [username/uuid] [...]
        String usernameOrUUID = args[1];
        PlayerData playerData;
        // get playerdata from given username / uuid
        // /gc purge username [...]
        if (mode.equalsIgnoreCase("username")) {
            playerData = PlayerData.PlayerDataByUsername.getOrDefault(usernameOrUUID, null);
            if (playerData == null) {
                ChatUtil.SendPrefixedMessage(sender, "§Username §f\"" + usernameOrUUID + "\"§c is not a player's username!§r");
                return;
            }
        }
        // /gc purge uuid [...]
        else if (mode.equalsIgnoreCase("uuid")) {
            playerData = PlayerData.GetPlayerDataByUUIDString(usernameOrUUID);
            if (playerData == null) {
                ChatUtil.SendPrefixedMessage(sender, "§cUUID §f\"" + usernameOrUUID + "\"§c is not a player's UUID!§r");
                return;
            }
        }
        else {
            ChatUtil.SendPrefixedMessage(sender, "§c\"" + mode + "\" is not a valid command for §f/purge§c!\n" +
                                                 "§cUsage: §f/gc purge [username/uuid] [...]§r");
            return;
        }

        // delete all references of player data
        PlayerData.Delete(playerData);

        ChatUtil.SendPrefixedMessage(sender, "§aPurged player §f\"" + playerData.Username + "\"§a.§r");
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc purge
        if (args.length == 1) {
            return List.of("username", "uuid", "playerdata", "countrydata");
        }

        // /gc purge 1
        if (args.length == 2) {
            return switch (args[0]) {
                // /gc purge username [usernames]
                case "username" -> PlayerData.AllAsUsernames();
                // /gc purge uuid [uuids]
                case "uuid" -> PlayerData.AllAsUUIDStrings();
                // /gc purge [...]
                default -> List.of();
            };
        }

        return new ArrayList<>();
    }
}
