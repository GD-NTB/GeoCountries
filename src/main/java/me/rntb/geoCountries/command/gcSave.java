package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class gcSave extends SubCommand {

    public gcSave(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Saves all plugin data in memory to the disk.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(gcSave::onConfirm,
                                                  sender,
                                                  new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        // save data collections
        ChatUtil.sendPrefixedMessage(sender, "§eSaving all data collections...");
        DataCollectionManager.save();
        ChatUtil.sendPrefixedMessage(sender, "§aSaved all data!");
    }
}
