package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcPurgeCitizenshipApplication extends GeoCommand {

    public gcPurgeCitizenshipApplication(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Purges all citizenship applications in memory.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[0]),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        int count = CitizenshipApplication.purge();
        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a CitizenshipApplication" + StringUtil.leadingS(count) + ".");
    }
}
