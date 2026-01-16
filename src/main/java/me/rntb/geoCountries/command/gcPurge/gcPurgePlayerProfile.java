package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class gcPurgePlayerProfile {

    public static void doCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcPurgePlayerProfile::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        int count = PlayerProfile.all.size();

        for (PlayerProfile pd : new ArrayList<>(PlayerProfile.all)) // new ArrayList as we are concurrently modifying
            PlayerProfile.delete(pd);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a PlayerProfile" + StringUtil.LeadingS(count) + ".");
    }
}
