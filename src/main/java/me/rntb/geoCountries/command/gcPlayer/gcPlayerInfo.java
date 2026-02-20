package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.DateUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcPlayerInfo extends SubSubCommand {

    public gcPlayerInfo(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile;
        // if no args, get player profile
        if (args.length == 0) {
            playerProfile = PlayerProfile.byCommandSender(sender);
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

        String onlineString = "§aOnline";
        if (Bukkit.getPlayer(playerProfile.username) == null) {
            long daysAgo = DateUtil.daysAgo(playerProfile.getOfflinePlayer().getLastSeen());
            onlineString = "§cLast seen §f" + daysAgo + "§c days ago";
        }

        long daysAgo = DateUtil.daysAgo(playerProfile.timeFirstJoined);
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== PLAYER INFO ==========
                         §a%s§f (%s§f)
                         §f> %s
                         §f> Joined on §2%s §8(%s day%s ago)
                         §6================================"""
                        .formatted(playerProfile.username,
                                   onlineString,
                                   rankAndCountryString,
                                   playerProfile.timeFirstJoinedAsString(),
                                   daysAgo, StringUtil.leadingS(daysAgo));
        ChatUtil.sendPrefixedMessage(sender, message);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.RequiredPermission))
            return List.of();
        return PlayerProfile.allAsUsernames(true);
    }
}
