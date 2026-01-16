package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand {

    public String DisplayName = "/gc command";
    public String RequiredPermission = "gc";
    public boolean ConsoleCanUse = true;
    public String HelpString = "";
    public String HelpPage = ""; // shown in /gc help [...]

    public SubCommand(String displayName, String requiredPermission, boolean consoleCanUse) {
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
        this.ConsoleCanUse = consoleCanUse;
    }

    public void onCommandEntered(CommandSender sender,  String[] args) {
        // if we are console and console can't use, escape
        if (!(sender instanceof Player) && !this.ConsoleCanUse) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly players can run §f%s§c!"
                                                 .formatted(this.DisplayName));
            return;
        }

        // if we are player and permission is needed and dont have permission, escape
        if (sender instanceof Player && RequiredPermission != null && !sender.hasPermission(RequiredPermission)) {
            ChatUtil.sendNoPermissionMessage(sender, this.DisplayName, this.RequiredPermission);
            return;
        }

        // else doCommand
        doCommand(sender, args);
    }

    public abstract void doCommand(CommandSender sender, String[] args);

    public abstract List<String> getTabCompletion(CommandSender sender,  String[] args);
}
