package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand {

    String DisplayName; // used in error messages and that
    Boolean ConsoleCanUse = true;
    String RequiredPermission = ""; // if no permission required, null
    public String HelpString = ""; // shown in /gc help
    public String HelpPage = ""; // shown in /gc help [...]

    public SubCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
        this.ConsoleCanUse = consoleCanUse;
    }

    public void onCommandEntered(@NotNull CommandSender sender, @NotNull String[] args) {
        // if we are console and console can't use, escape
        if (!(sender instanceof Player) && !this.ConsoleCanUse) {
            ChatUtil.SendPrefixedMessage(sender, String.format("§cOnly players can run §f%s§c!",
                                                               this.DisplayName));
            return;
        }

        // if we are player and permission is needed and dont have permission, escape
        if (sender instanceof Player && RequiredPermission != null && !sender.hasPermission(RequiredPermission)) {
            ChatUtil.SendNoPermissionMessage(sender, this.DisplayName, this.RequiredPermission);
            return;
        }

        // else doCommand
        doCommand(sender, args);
    }

    public abstract void doCommand(CommandSender sender, String[] args);

    public abstract List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args);
}
