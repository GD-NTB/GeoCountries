package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcCountryCitizens {

    // todo: break into pages (page number as arg)
    public static void onCommand(CommandSender sender,  String[] args) {
        Country country;
        // if no args, country = player's country
        if (args.length == 0) {
            Player player = (Player) sender;
            PlayerProfile playerProfile = PlayerProfile.get(player);
            country = playerProfile.getCitizenship();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY CITIZENS ==========
                                                     §cYou do not have citizenship of any country.
                                                     §cDo §f/gc country citizens [country]§c to get a list of a country's citizens.
                                                     §6=================================""");
                return;
            }
        }
        else {
            String countryName = String.join(" ", args);

            country = Country.byName.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== COUNTRY CITIZENS ==========\n");

        int citizenCount = country.citizenCount();
        if (citizenCount == 0) {
            sb.append("§cThere are no citizens of this country.\n");
        }
        else {
            sb.append("§e%s§f has §e%d§f citizen%s:\n"
                      .formatted(country.name, citizenCount, StringUtil.LeadingS(citizenCount)));
            for (PlayerProfile citizen : country.citizensSortedByRank()) {
                if (citizen != null)
                    sb.append("§f> §a%s§f (§e%s§f)\n"
                              .formatted(citizen.username, citizen.getRankString()));
                else
                    sb.append("§f> §cNone\n");
            }
        }
        sb.append("§6======================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
