package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

public class gcPurgeUsername {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou need to specify the username of the player as it appears in the data collections.");
            return;
        }

        String username = args[0];
        PlayerProfile player = PlayerProfile.byUsername.get(username);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + username + "§c could not be found!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcPurgeUsername::onConfirm,
                                                   sender,
                                                   new String[] { username }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.byUsername.get(args[0]);
        PlayerProfile.delete(player);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + player.username + "§a.");
    }
}
