package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class gcDump extends SubCommand {

    public gcDump(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Dumps plugin info.";
        this.HelpPage   = """
                          §f/gc dump§a: Dumps some plugin info into the chat for easier debugging.
                          """;;
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc dump
        ChatUtil.SendPrefixedMessage(sender, ChatUtil.NewlineIfPrefixIsEmpty() +
                                             "PlayerData.All(" + PlayerData.All.toArray().length + ")\n" +
                                             "----------\n" +
                                             "PlayerDataByUsername(" + PlayerData.PlayerDataByUsername.keySet().toArray().length + "), PlayerDataByUUID(" + PlayerData.PlayerDataByUUID.keySet().toArray().length + ")\n" +
                                             "----------\n" +
                                             "CountryData.All(" + CountryData.All.toArray().length + ")");
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
