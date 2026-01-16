package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

public class gcPurgeUsername {

    public static void doCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou need to specify the username of the player as it appears in the data collections.");
            return;
        }

        String username = args[1];
        PlayerProfile player = PlayerProfile.byUsername.get(username);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cUsername §f" + username + "§c is not a player's username!");
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
