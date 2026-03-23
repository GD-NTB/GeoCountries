package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcLoad extends GeoCommand {

    public gcLoad(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Loads all plugin data from the disk to memory.";
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

        ChatUtil.sendPrefixedMessage(sender, "§eTriggering integration init...");
        // tooltips and colours and stuff could have changed, so we need to redo
        IntegrationManager.init();

        ChatUtil.sendPrefixedMessage(sender, "§aLoaded all data!");
    }
}
