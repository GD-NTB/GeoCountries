package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcCountryInfo {

    public static void onCommand(CommandSender sender,  String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to get info of!");
            return;
        }

        String countryName = String.join(" ", args);

        Country country = Country.byName.get(countryName);
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        PlayerProfile leader = country.getLeader();

        String sb = ChatUtil.newlineIfPrefixIsEmpty() +
                """
                §6========== COUNTRY INFO ==========
                §a%s§f
                > §eLeader§f: %s§f
                > §eCitizens§f: %s§f
                §6================================="""
                .formatted(country.name, leader != null ? leader.username : "§cNone", country.citizenCount());
        ChatUtil.sendPrefixedMessage(sender, sb);
    }
}
