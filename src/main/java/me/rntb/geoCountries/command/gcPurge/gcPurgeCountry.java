package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class gcPurgeCountry extends SubSubCommand {

    public gcPurgeCountry(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
        this.HelpString = "Purges all Country data collections.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    @Override
    public void onConfirm(CommandSender sender, String[] args) {
        int count = Country.all.size();
        for (Country cd : new ArrayList<>(Country.all)) { // new ArrayList as we are concurrently modifying
            Country.delete(cd);
        }

        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a Countr" + (count > 1 ? "ies" : "y") + ".");
    }
}
