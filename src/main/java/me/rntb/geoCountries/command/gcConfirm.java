package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class gcConfirm extends SubCommand {

    public gcConfirm(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Confirms the last command/action you were asked to confirm.";
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
        confirmation.function.accept(confirmation.sender, confirmation.args);

        // remove sender from waiting list
        Confirmation.stopWaiting(uuid, Confirmation.StopWaitingEvent.CONFIRMED, false);
    }
}
