package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class gcAdminSetPlayerCountry {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to change the country of!");
            return;
        }

        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new country you want the player to be a citizen of!");
            return;
        }

        String playerName = args[0];
        PlayerProfile player = PlayerProfile.byUsername.get(playerName);

        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        String countryName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (countryName.equals("null")) {
            // set country
            player.clearCitizenship();
        }
        else {
            Country country = Country.byName.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }

            // set country
            player.setCitizenship(country, player.rank != PlayerProfile.PlayerRank.NONE ? player.rank : PlayerProfile.PlayerRank.CITIZEN);
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet player country!");
    }
}
