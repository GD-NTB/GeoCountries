package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class gcPlayerInfo {

    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the player you want to get info of!");
            return;
        }

        PlayerData player = PlayerData.PlayerDataByUsername.get(args[0]);
        if (player == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cPlayer \"§f" + args[0] + "§c\" could not be found!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                "§6========== PLAYER INFO ==========\n");
        sb.append("§a").append(player.Username).append("\n");

        // show rank of player in country
        CountryData country = player.getCountry();

        if (player.Rank == PlayerData.PlayerRank.NONE) {
            sb.append("§f> §Stateless");
        }
        else {
            sb.append("§f> §e").append(player.getRankString()).append("§f of: ").append(country.Name);
        }
        switch (player.Rank) {
            case PlayerData.PlayerRank.LEADER:
                sb.append("§f> §eLeader of§f: ").append(country.Name);
                break;
            case PlayerData.PlayerRank.CITIZEN:
                sb.append("§f> §eCitizen of§f: ").append(country.Name);
                break;
            case PlayerData.PlayerRank.NONE:
                sb.append("§f> §Stateless");
                break;
        }
        sb.append("\n");

        sb.append("§6================================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }
}
