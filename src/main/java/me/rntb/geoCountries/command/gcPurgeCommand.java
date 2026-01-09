package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class gcPurgeCommand extends SubCommand {

    public gcPurgeCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
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
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
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
                gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                                Triple.of(gcPurgeCommand::gcPurgePlayerDataConfirmed,
                                                        sender,
                                                        new String[] { }));
                return;
            }

            //gc purge countrydata
            else if (mode.equalsIgnoreCase("countrydata")) {
                // start waiting for confirm
                gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                                Triple.of(gcPurgeCommand::gcPurgeCountryDataConfirmed,
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
            gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                            Triple.of(gcPurgeCommand::gcPurgeUsernameConfirmed,
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
            gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                            Triple.of(gcPurgeCommand::gcPurgeUUIDConfirmed,
                                                    sender,
                                                    new String[] { usernameOrUUID })); // 0 = uuid
        }
        else {
            ChatUtil.SendPrefixedMessage(sender, "§c\"" + mode + "\" is not a valid command for §f/purge§c!\n" +
                                                 "Usage: §f/gc purge [...]");
            return;
        }
    }

    // ---------- /gc purge playerdata ----------
    public static void gcPurgePlayerDataConfirmed(CommandSender sender, String[] args) {
        int allLength = PlayerData.All.toArray().length;
        for (PlayerData pd : new ArrayList<>(PlayerData.All)) { // new ArrayList as we are concurrently modifying
            PlayerData.Delete(pd);
        }
        ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a PlayerDatas.");
    }

    // ---------- /gc purge countrydata ----------
    public static void gcPurgeCountryDataConfirmed(CommandSender sender, String[] args) {
        int allLength = CountryData.All.toArray().length;
        for (CountryData cd : new ArrayList<>(CountryData.All)) { // new ArrayList as we are concurrently modifying
            CountryData.Delete(cd);
        }
        ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a CountryDatas.");
    }

    // ---------- /gc purge username ----------
    public static void gcPurgeUsernameConfirmed(CommandSender sender, String[] args) {
        PlayerData playerData = PlayerData.PlayerDataByUsername.get(args[0]);
        PlayerData.Delete(playerData);

        ChatUtil.SendPrefixedMessage(sender, "§aPurged player §f\"" + playerData.Username + "\"§a.");
    }


    // ---------- /gc purge uuid ----------
    public static void gcPurgeUUIDConfirmed(CommandSender sender, String[] args) {
        PlayerData playerData = PlayerData.GetPlayerDataByUUIDString(args[0]);
        PlayerData.Delete(playerData);

        ChatUtil.SendPrefixedMessage(sender, "§aPurged player §f\"" + playerData.Username + "\"§a.");
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
