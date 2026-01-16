package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class gcCountryList {

    public static void onCommand(CommandSender sender,  String[] args) {
        // todo: pages
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                "§6========== COUNTRY LIST ==========\n");

        if (Country.all.isEmpty()) {
            sb.append("§cThere are no countries.\n");
        }
        else {
            for (Country country : Country.all) {
                PlayerProfile leader = country.getLeader();
                int citizens = country.citizenCount();
                sb.append("§a%s§f (§eLeader§f: %s, §eCitizens§f: %s)\n"
                          .formatted(country.name,
                                     leader != null ? country.getLeader().username : "§cNone",
                                     citizens != 0 ? citizens : "§c0"));
            }
        }
        sb.append("§6=================================");
        ChatUtil.sendPrefixedMessage(sender, sb.toString());
    }
}
