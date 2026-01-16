package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

public class gcPurgeUUID {

    public static void doCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou need to specify the UUID of the player as it appears in the data collections.");
            return;
        }

        String uuid = args[1];
        PlayerProfile player = PlayerProfile.byUUIDString(uuid);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cUUID §f" + uuid + "§ is not a player's UUID!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcPurgeUUID::onConfirm,
                                                   sender,
                                                   new String[] { uuid }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.byUUIDString(args[0]);
        PlayerProfile.delete(player);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + player.username + "§a.");
    }
}
