package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcSave extends GeoCommand {

    public gcSave(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Saves all plugin data in memory to the disk.";
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
        // save data collections
        ChatUtil.sendPrefixedMessage(sender, "§eSaving all data collections...");
        DataCollectionManager.save();
        ChatUtil.sendPrefixedMessage(sender, "§aSaved all data!");
    }
}
