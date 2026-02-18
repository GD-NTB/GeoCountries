package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcLoad extends SubCommand {

    public gcLoad(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Loads all plugin data from disk.";
        this.HelpPage   = """
                          §f/gc load: §aLoads all saved plugin data from the disk to memory.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(gcLoad::onConfirm,
                                                  sender,
                                                  new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        // load data collections
        ChatUtil.sendPrefixedMessage(sender, "§eLoading all data collections...");
        DataCollectionManager.init();
        ChatUtil.sendPrefixedMessage(sender, "§aLoaded all data!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
