package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class gcDump extends SubCommand {

    public gcDump(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Dumps plugin info.";
        this.HelpPage   = """
                          §f/gc dump: §aDumps some plugin info into the chat for easier debugging.""";;
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc dump
        ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                             "PlayerProfile.All(" + PlayerProfile.all.size() + ")\n" +
                                             "----------\n" +
                                             "ByUsername(" + PlayerProfile.byUsername.size() + "), ByUUID(" + PlayerProfile.byUUID.keySet().toArray().length + ")\n" +
                                             "----------\n" +
                                             "Country.All(" + Country.all.size() + ")");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
