package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class gcCountryInfo {

    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to get info of!");
            return;
        }

        String countryName = String.join(" ", args);

        CountryData country = CountryData.CountryDataByName.get(countryName);
        if (country == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cCountry \"§f" + countryName + "§c\" could not be found!");
            return;
        }

        PlayerData leader = country.getLeader();

        String sb = ChatUtil.NewlineIfPrefixIsEmpty() +
                "§6========== COUNTRY INFO ==========\n" +
                String.format("§a%s§f\n" +
                                "> §eLeader§f: %s§f\n" +
                                "> §eCitizens§f: %s§f\n",
                        country.Name, leader != null ? leader.Username : "§cNone", country.CitizenCount()) +
                "§6=================================";
        ChatUtil.SendPrefixedMessage(sender, sb);
    }
}
