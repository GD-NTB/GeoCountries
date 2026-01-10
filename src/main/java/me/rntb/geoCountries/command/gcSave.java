package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class gcSave extends SubCommand {

    public gcSave(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Saves all plugin data.";
        this.HelpPage   = """
                          §f/gc save§a: Saves all data collections (PlayerData, etc).""";
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc save
        // start waiting for confirm
        gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                 Triple.of(gcSave::onConfirm,
                                           sender,
                                           new String[] { }));
    }

    private static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        // save data collections
        ChatUtil.SendPrefixedMessage(sender, "§eSaving all data collections...");
        DataCollectionManager.SaveCollections();

        ChatUtil.SendPrefixedMessage(sender, "§aSaved all data!");
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
