package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class gcLoad extends GeoCommand {

    public gcLoad(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Loads all saved plugin data from the disk to memory.";
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

    private void onConfirm(CommandSender sender, String[] args) {
        // load data collections
        ChatUtil.sendPrefixedMessage(sender, "§eLoading all data collections...");
        DataCollectionManager.init();
        ChatUtil.sendPrefixedMessage(sender, "§aLoaded all data!");
    }
}
