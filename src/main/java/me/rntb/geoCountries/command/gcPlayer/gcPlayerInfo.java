package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcPlayerInfo {

    public static void onCommand(CommandSender sender,  String[] args) {
        PlayerProfile playerProfile;
        // if no args, get player profile
        if (args.length == 0) {
            Player player = (Player) sender;
            playerProfile = PlayerProfile.get(player);
        }
        // else get specific player info
        else {
            playerProfile = PlayerProfile.byUsername.get(args[0]);
            if (playerProfile == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + args[0] + "§c could not be found!");
                return;
            }
        }

        String rankAndCountryString = "§cStateless";
        if (playerProfile.hasCitizenship()) {
            Country country = playerProfile.getCitizenship();
            rankAndCountryString = "§e%s§f of §e%s"
                             .formatted(playerProfile.getRankString(), country != null ? country.name : "§cNone");
        }
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== PLAYER INFO ==========
                         §a%s§f
                         §f> %s
                         §f> Joined on §2%s
                         §6================================="""
                        .formatted(playerProfile.username,
                                   rankAndCountryString,
                                   playerProfile.timeFirstJoinedAsString());
        ChatUtil.sendPrefixedMessage(sender, message);
    }
}
