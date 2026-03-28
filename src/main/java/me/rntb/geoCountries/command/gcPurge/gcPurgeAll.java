package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcPurgeAll extends GeoCommand {

    public gcPurgeAll(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Purges all data collections.");
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
        DataCollectionManager.purgeAll();
        ChatUtil.sendPrefixedMessage(sender, "§aPurged all data collections!");
    }
}
