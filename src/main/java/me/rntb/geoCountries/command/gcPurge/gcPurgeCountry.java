package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class gcPurgeCountry {

    public static void doCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcPurgeCountry::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        int count = Country.all.size();

        for (Country cd : new ArrayList<>(Country.all)) // new ArrayList as we are concurrently modifying
            Country.delete(cd);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a Countr" + (count > 1 ? "ies" : "y") + ".");
    }
}
