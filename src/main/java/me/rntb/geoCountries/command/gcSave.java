package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class gcSave extends SubCommand {

    public gcSave(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Saves all plugin data.";
        this.HelpPage   = """
                          §f/gc save: §aSaves all plugin data in memory to the disk.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc save
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcSave::onConfirm,
                                                  sender,
                                                  new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        // save data collections
        ChatUtil.sendPrefixedMessage(sender, "§eSaving all data collections...");
        DataCollectionManager.save();

        ChatUtil.sendPrefixedMessage(sender, "§aSaved all data!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
