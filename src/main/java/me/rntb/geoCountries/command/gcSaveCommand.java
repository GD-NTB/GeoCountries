package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class gcSaveCommand extends SubCommand {

    public gcSaveCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Save all data (data collections, etc).";
        this.HelpPage   = "§f/gc save§a: Save all data.§r";
    }

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc save
        // start waiting for confirm
        gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                        Triple.of(gcSaveCommand::doCommandConfirmed,
                                                  sender,
                                                  new String[] { }));
    }

    static void doCommandConfirmed(CommandSender sender, String[] args) {
        // save data collections
        ChatUtil.SendPrefixedMessage(sender, "§eSaving all data collections...§r");
        DataCollectionManager.SaveCollections();

        ChatUtil.SendPrefixedMessage(sender, "§aSaved all data!§r");
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
