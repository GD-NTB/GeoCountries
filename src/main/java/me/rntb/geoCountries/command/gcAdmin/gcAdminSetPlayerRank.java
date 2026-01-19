package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcAdminSetPlayerRank {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to change the rank of!");
            return;
        }

        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new rank you want the player to have!");
            return;
        }

        String playerName = args[0];
        PlayerProfile player = PlayerProfile.byUsername.get(playerName);

        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // get rank
        PlayerProfile.PlayerRank rank;
        try {
            rank = PlayerProfile.PlayerRank.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            ChatUtil.sendPrefixedMessage(sender, "§cRank §f" + args[1] + "§c could not be found!");
            return;
        }

        // set rank
        player.setRank(rank);

        ChatUtil.sendPrefixedMessage(sender, "§aSet player rank!");
    }
}
