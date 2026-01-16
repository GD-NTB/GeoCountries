package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class gcConfirm extends SubCommand {

    public gcConfirm(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Confirms a command or action.";
        this.HelpPage   = """
                          §f/gc confirm: §aConfirms the last command/action you were asked to type §f/gc confirm§a for.""";
    }

    @Override
    public void doCommand(CommandSender sender, String[] args) {
        UUID uuid = UuidUtil.GetUUIDOfCommandSender(sender);

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

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
