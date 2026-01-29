package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcDump extends SubCommand {

    public gcDump(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Dumps plugin info.";
        this.HelpPage   = """
                          §f/gc dump: §aDumps some plugin info into the chat for easier debugging.""";;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, """
                                             %sPlayerProfile.all(%s)
                                             ----------
                                             byUsername(%s), byUUID(%s)
                                             ----------
                                             CitizenshipApplication.sendByApplicant(%s)
                                             ----------
                                             Country.all(%s)
                                             """
                                             .formatted(ChatUtil.newlineIfPrefixIsEmpty(),
                                                        PlayerProfile.all.size(),
                                                        PlayerProfile.byUsername.size(), PlayerProfile.byUUID.size(),
                                                        CitizenshipApplication.sentByApplicant.size(),
                                                        Country.all.size()));
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
