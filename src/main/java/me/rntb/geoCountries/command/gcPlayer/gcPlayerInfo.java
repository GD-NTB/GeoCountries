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

        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== PLAYER INFO ==========\n");
        sb.append("§a").append(playerProfile.username).append("\n");

        // show rank of player in country
        Country country = playerProfile.getCitizenship();

        if (playerProfile.rank == PlayerProfile.PlayerRank.NONE) {
            sb.append("§f> §cStateless");
        }
        else {
            sb.append("§f> §e").append(playerProfile.getRankString()).append("§f of §e").append(country != null ? country.name : "§cNone");
        }

        sb.append("\n§6================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
