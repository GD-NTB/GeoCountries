package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class gcPurgeCitizenshipApplication extends SubSubCommand {

    public gcPurgeCitizenshipApplication(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
        this.HelpString = "Purges all citizenship applications in memory.";
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
        int count = CitizenshipApplication.sentAll.size();

        for (CitizenshipApplication ca : new ArrayList<>(CitizenshipApplication.sentAll)) { // new ArrayList as we are concurrently modifying
            ca.deleteSent();
        }

        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a CitizenshipApplication" + StringUtil.leadingS(count) + ".");
    }
}
