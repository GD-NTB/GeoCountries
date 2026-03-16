package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcPlayerInfo extends GeoCommand {

    public gcPlayerInfo(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Displays info about a particular player.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile;
        // if no args, get player profile
        if (args.length == 0)
            playerProfile = PlayerProfile.get(sender);
        // else get specific player info
        else {
            playerProfile = PlayerProfile.get(args[0]);
            if (playerProfile == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + args[0] + "§c could not be found!");
                return;
            }
        }

        String positionAndCountryString;
        if (playerProfile.hasCitizenship())
            positionAndCountryString = "§e%s§f of §e%s"
                                       .formatted(playerProfile.getPositionString(),
                                                  playerProfile.getCitizenshipCountry().getFullDisplayName());
        else
            positionAndCountryString = "§cStateless";

        String onlineString = "§aOnline";
        if (Bukkit.getPlayer(playerProfile.getUsername()) == null) {
            long onlineDaysAgo = TimeUtil.daysAgo(playerProfile.getOfflinePlayer().getLastSeen());
            onlineString = "§cLast online §f" + onlineDaysAgo + "§c day" + StringUtil.leadingS(onlineDaysAgo) + " ago";
        }

        long createdDaysAgo = TimeUtil.daysAgo(playerProfile.getTimeCreated());
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== PLAYER INFO ==========
                         §a%s§f (%s§f)
                         §f> §e%s
                         §f> Joined on §2%s §8(%s day%s ago)
                         §6================================"""
                        .formatted(playerProfile.getUsername(),
                                   onlineString,
                                   positionAndCountryString,
                                   playerProfile.getTimeCreatedAsString(), createdDaysAgo, StringUtil.leadingS(createdDaysAgo));

        ChatUtil.sendPrefixedMessage(sender, message);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUsernames(true) : List.of();
    }
}
