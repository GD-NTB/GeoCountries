package me.rntb.geoCountries.command;

import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcConfirm extends GeoCommand {

    public gcConfirm(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Confirms a pending command/action.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        UUID uuid = UuidUtil.getUUIDOfCommandSender(sender);

        // if sender not being waited on, escape
        if (!Confirmation.isWaiting(uuid)) {
            ChatUtil.sendPrefixedMessage(sender, "§cNo command was waiting to be confirmed.");
            return;
        }

        // execute
        Confirmation confirmation = Confirmation.get(uuid);
        confirmation.getFunction().accept(confirmation.getSender(), confirmation.getArgs());

        // remove sender from waiting list
        Confirmation.stopWaiting(uuid, Confirmation.StopWaitingEvent.CONFIRMED, false);
    }
}
