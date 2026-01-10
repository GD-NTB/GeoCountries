package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.command.gcConfirm;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class gcPurge extends SubCommand {

    public gcPurge(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Purges (deletes) plugin data.";
        this.HelpPage   = """
                          §f/gc purge [...]§a: Purges (deletes) specific data within the plugin's persistent storage, such as data collections, etc.
                          §cShould be used very very rarely!
                          §f> playerdata: §aPurges all PlayerData data collections.
                          §f> countrydata: §aPurges all CountryData data collections.
                          §f> username [username]: §aPurges a PlayerData by username.
                          §f> uuid [uuid]: §aPurges a PlayerData by UUID.""";
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc purge
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aPurges (deletes) plugin data.\n" +
                                                 "Usage: §f/gc purge [...]");
            return;
        }

        String mode = args[0];

        // /gc purge [username/uuid/playerdata/countrydata]
        if (args.length == 1) {
            // /gc purge username
            if (mode.equalsIgnoreCase("username")) {
                ChatUtil.SendPrefixedMessage(sender, "§cYou need to specify the username of the player as it appears in the data collections.\n" +
                                                     "Usage: §f/gc purge username [...]");
                return;
            }

            // /gc purge uuid
            else if (mode.equalsIgnoreCase("uuid")) {
                ChatUtil.SendPrefixedMessage(sender, "§cYou need to specify the UUID of the player as it appears in the data collections.\n" +
                                                     "Usage: §f/gc purge uuid [...]");
                return;
            }

            // /gc purge playerdata
            else if (mode.equalsIgnoreCase("playerdata")) {
                // start waiting for confirm
                gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                         Triple.of(gcPurgePlayerData::onConfirm,
                                                 sender,
                                                 new String[] { }));
                return;
            }

            //gc purge countrydata
            else if (mode.equalsIgnoreCase("countrydata")) {
                // start waiting for confirm
                gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                         Triple.of(gcPurgeCountryData::onConfirm,
                                                 sender,
                                                 new String[] { }));
                return;
            }

            // gc [xxx]
            else {
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/purge§c!\n" +
                                                     "Usage: §f/gc purge [...]");
                return;
            }
        }

        // /gc purge [username/uuid] [...]
        String usernameOrUUID = args[1];
        PlayerData playerData;
        // get playerdata from given username / uuid
        // /gc purge username [...]
        if (mode.equalsIgnoreCase("username")) {
            playerData = PlayerData.PlayerDataByUsername.get(usernameOrUUID);
            if (playerData == null) {
                ChatUtil.SendPrefixedMessage(sender, "§Username §f\"" + usernameOrUUID + "\"§c is not a player's username!");
                return;
            }

            // start waiting for confirm
            gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                     Triple.of(gcPurgeUsername::onConfirm,
                                             sender,
                                             new String[] { usernameOrUUID })); // 0 = username
        }
        // /gc purge uuid [...]
        else if (mode.equalsIgnoreCase("uuid")) {
            playerData = PlayerData.GetPlayerDataByUUIDString(usernameOrUUID);
            if (playerData == null) {
                ChatUtil.SendPrefixedMessage(sender, "§cUUID §f\"" + usernameOrUUID + "\"§c is not a player's UUID!");
                return;
            }

            // start waiting for confirm
            gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                     Triple.of(gcPurgeUUID::onConfirm,
                                             sender,
                                             new String[] { usernameOrUUID })); // 0 = uuid
        }
        else {
            ChatUtil.SendPrefixedMessage(sender, "§c\"" + mode + "\" is not a valid command for §f/purge§c!\n" +
                                                 "Usage: §f/gc purge [...]");
            return;
        }
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            // /gc purge 1
            case 1 -> Stream.of("username", "uuid", "playerdata", "countrydata").filter(x -> sender.hasPermission("gc.purge." + x)).toList();
            // /gc purge [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc purge username [usernames]
                    case "username" -> sender.hasPermission("username") ? PlayerData.AllAsUsernames() : List.of();
                    // /gc purge uuid [uuids]
                    case "uuid" -> sender.hasPermission("uuid") ? PlayerData.AllAsUUIDStrings() : List.of();
                    // /gc purge [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
