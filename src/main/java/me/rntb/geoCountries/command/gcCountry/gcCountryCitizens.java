package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;

public class gcCountryCitizens {

    // todo: break into pages (page number as arg)
    public static void onCommand(CommandSender sender,  String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to get the list of citizens of!");
            return;
        }

        String countryName = String.join(" ", args);

        Country country = Country.byName.get(countryName);
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== COUNTRY CITIZENS ==========\n");

        int citizenCount = country.citizenCount();
        if (citizenCount == 0) {
            sb.append("§cThere are no citizens of this country.\n");
        }
        else {
            sb.append("§e%s§f has §e%d§f citizen:%s\n"
                      .formatted(country.name, citizenCount, StringUtil.LeadingS(citizenCount)));
            for (PlayerProfile citizen : country.citizensSortedByRank()) {
                sb.append("§f> §a%s§f (§e%s§f)"
                          .formatted(citizen.username, citizen.getRankString()));
            }
        }
        sb.append("\n§6======================================");
        ChatUtil.sendPrefixedMessage(sender, sb.toString());
    }
}
