package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class gcCountryCitizens {

    // todo: break into pages (page number as arg)
    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to get the list of citizens of!");
            return;
        }

        String countryName = String.join(" ", args);

        CountryData country = CountryData.CountryDataByName.get(countryName);
        if (country == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cCountry \"§f" + countryName + "§c\" could not be found!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                "§6========== COUNTRY CITIZENS ==========");

        int citizenCount = country.CitizenCount();
        if (citizenCount == 0) {
            sb.append("§cThere are no citizens of this country.\n");
        }
        else {
            sb.append("§e").append(citizenCount).append(" §fcitizens:\n");
            for (PlayerData citizen : country.CitizensSortedByRank()) {
                sb.append(String.format("§f> §a%s§f (§e%s§f)\n",
                        citizen.Username, citizen.getRankString()));
            }
        }
        sb.append("§6======================================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }
}
