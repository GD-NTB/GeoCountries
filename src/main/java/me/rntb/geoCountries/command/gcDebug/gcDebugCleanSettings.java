package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcDebugCleanSettings {

    public static void onCommand(CommandSender sender, String[] args) {
        // clean country settings
        for (Country country : Country.all) {
            country.purgeBrokenSettingsAndLoadMetaData();
        }

        ChatUtil.sendPrefixedMessage(sender, "§aCleaned settings!");
    }
}
