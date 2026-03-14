package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.TimeUtil;
import me.rntb.geoCountries.util.StringUtil;
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

        String positionAndCountryString = "§cStateless";
        if (playerProfile.hasCitizenship()) {
            Country country = playerProfile.getCitizenshipCountry();
            positionAndCountryString = "§e%s§f of §e%s"
                                       .formatted(playerProfile.getPositionString(), country != null ? country.getName() : "§cNone");
        }

        String onlineString = "§aOnline";
        if (Bukkit.getPlayer(playerProfile.getUsername()) == null) {
            long daysAgo = TimeUtil.daysAgo(playerProfile.getOfflinePlayer().getLastSeen());
            onlineString = "§cLast seen §f" + daysAgo + "§c days ago";
        }

        long daysAgo = TimeUtil.daysAgo(playerProfile.getTimeCreated());
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== PLAYER INFO ==========
                         §a%s§f (%s§f)
                         §f> %s
                         §f> Joined on §2%s §8(%s day%s ago)
                         §6================================"""
                        .formatted(playerProfile.getUsername(),
                                   onlineString,
                                   positionAndCountryString,
                                   playerProfile.getTimeCreatedAsString(), daysAgo, StringUtil.leadingS(daysAgo));

        ChatUtil.sendPrefixedMessage(sender, message);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUsernames(true) : List.of();
    }
}
