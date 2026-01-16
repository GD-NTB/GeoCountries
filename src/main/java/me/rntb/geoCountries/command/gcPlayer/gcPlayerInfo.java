package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcPlayerInfo {

    public static void onCommand(CommandSender sender,  String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to get info of!");
            return;
        }

        PlayerProfile player = PlayerProfile.byUsername.get(args[0]);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + args[0] + "§c could not be found!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                "§6========== PLAYER INFO ==========\n");
        sb.append("§a").append(player.username).append("\n");

        // show rank of player in country
        Country country = player.getCitizenship();

        if (player.rank == PlayerProfile.PlayerRank.NONE) {
            sb.append("§f> §cStateless");
        }
        else {
            sb.append("§f> §e").append(player.getRankString()).append("§f of a").append(country.name);
        }

        sb.append("\n§6================================");
        ChatUtil.sendPrefixedMessage(sender, sb.toString());
    }
}
