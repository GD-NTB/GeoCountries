package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.types.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class gcCancel extends SubCommand {

    public gcCancel(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Cancels a command or action.";
        this.HelpPage   = """
                          §f/gc cancel: §aCancels the last command you were trying to do.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        UUID uuid = UuidUtil.getUUIDOfCommandSender(sender);

        // if sender not being waited on, escape
        if (!Confirmation.isWaiting(uuid) && !Response.isWaiting(uuid)) {
            ChatUtil.sendPrefixedMessage(sender, "§cNo command was there to be cancelled.");
            return;
        }

        // cancel
        Confirmation.stopWaiting(uuid, Confirmation.StopWaitingEvent.CANCELLED, true);
        Response.stopWaiting(uuid, Response.StopWaitingEvent.CANCELLED, true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
