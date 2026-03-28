package me.rntb.geoCountries.command;

import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcCancel extends GeoCommand {

    public gcCancel(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Cancels a pending command/action.");
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
}
