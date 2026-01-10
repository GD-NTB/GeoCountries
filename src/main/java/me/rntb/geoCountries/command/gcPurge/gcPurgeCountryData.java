package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class gcPurgeCountryData {

    public static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        int allLength = CountryData.All.toArray().length;
        for (CountryData cd : new ArrayList<>(CountryData.All)) { // new ArrayList as we are concurrently modifying
            CountryData.Delete(cd);
        }
        ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a CountryDatas.");
    }
}
