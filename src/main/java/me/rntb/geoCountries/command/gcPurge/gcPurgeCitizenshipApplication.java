package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class gcPurgeCitizenshipApplication {

    public static void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(gcPurgeCitizenshipApplication::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        int count = CitizenshipApplication.sentAll.size();

        for (CitizenshipApplication ca : new ArrayList<>(CitizenshipApplication.sentAll)) // new ArrayList as we are concurrently modifying
            CitizenshipApplication.deleteSent(ca);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a CitizenshipApplication" + StringUtil.leadingS(count) + ".");
    }
}
