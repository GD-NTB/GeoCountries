package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.types.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class gcCancel extends SubCommand {

    public gcCancel(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Cancels a command or action.";
        this.HelpPage   = """
                          §f/gc cancel: §aCancels the last command you were trying to do.""";
    }

    @Override
    public void doCommand(CommandSender sender, String[] args) {
        // if console, uuid=0000..., else get player uuid
        UUID uuid = UuidUtil.GetUUIDOfCommandSender(sender);

        // cancel command
        if (!Confirmation.isWaiting(uuid) && !Response.isWaiting(uuid)) {
            ChatUtil.sendPrefixedMessage(sender, "§cNo command was there to be cancelled.");
            return;
        }

        Confirmation.stopWaiting(uuid, Confirmation.StopWaitingEvent.CANCELLED, true);
        Response.stopWaiting(uuid, Response.StopWaitingEvent.CANCELLED, true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
