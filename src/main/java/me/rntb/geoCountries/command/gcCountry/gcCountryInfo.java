package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcCountryInfo {

    public static void onCommand(CommandSender sender,  String[] args) {
        Country country;
        // if no args, get player country
        if (args.length == 0) {
            Player player = (Player) sender;
            PlayerProfile playerProfile = PlayerProfile.get(player);
            country = playerProfile.getCitizenship();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY INFO ==========
                                                     §cYou do not have citizenship of any country.
                                                     §cDo §f/gc country info [country]§c to get country info.
                                                     §6=================================""");
                return;
            }
        }
        // else get specific country
        else {
            String countryName = String.join(" ", args);
            country = Country.byName.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        PlayerProfile leader = country.getLeader();

        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== COUNTRY INFO ==========
                         §a%s§f
                         §f> §eLeader§f: %s
                         §f> §eCitizens§f: %s
                         §f> Created on §2%s
                         §6================================="""
                         .formatted(country.name,
                                    leader != null ? leader.username : "§cNone",
                                    country.citizenCount(),
                                    country.timeCreatedAsString());
        ChatUtil.sendPrefixedMessage(sender, message);
    }
}
