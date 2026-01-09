package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static me.rntb.geoCountries.command.gcConfirmCommand.IsWaitingForSender;

public class gcCancelCommand extends SubCommand {

    public gcCancelCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Cancels a command or action.";
        this.HelpPage   = """
                          §f/gc cancel§a: Cancels the last command you were asked to type §f/gc confirm§a for.
                          """;
    }

    @Override
    void doCommand(CommandSender sender, String[] args) {
        // if console, uuid=0000..., else get player uuid
        UUID uuid = UuidUtil.GetUUIDOfCommandSender(sender);

        // if sender not being waited on, escape
        if (!IsWaitingForSender(uuid)) {
            ChatUtil.SendPrefixedMessage(sender, "§cNo command was waiting to be confirmed.");
            return;
        }

        ChatUtil.SendPrefixedMessage(sender, "§eCancelled the command.");
        // remove sender from waiting list
        gcConfirmCommand.StopWaitingForSender(uuid);
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
