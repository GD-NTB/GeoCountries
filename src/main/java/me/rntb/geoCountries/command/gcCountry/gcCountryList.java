package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class gcCountryList {

    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // todo: pages
        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                "§6========== COUNTRY LIST ==========\n");

        if (CountryData.All.isEmpty()) {
            sb.append("§cThere are no countries.\n");
        }
        else {
            for (CountryData country : CountryData.All) {
                PlayerData leader = country.getLeader();
                int citizens = country.CitizenCount();
                sb.append(String.format("§a%s§f (§eLeader§f: %s, §eCitizens§f: %s)\n",
                        country.Name, leader != null ? country.getLeader().Username : "§cNone", citizens != 0 ? citizens : "§c0"));
            }
        }
        sb.append("§6=================================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }
}
