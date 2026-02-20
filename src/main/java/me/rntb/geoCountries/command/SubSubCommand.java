package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubSubCommand {

    public String Name;
    public String DisplayName;
    public String RequiredPermission;

    public SubSubCommand(String name, String displayName, String requiredPermission) {
        this.Name = name;
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
    }

    public String HelpString = "No help available."; // shown in /gc help [subcommand] [...]

    public final void onCommandEntered(CommandSender sender, String[] args) {
        //  if no permission, escape
        if (RequiredPermission != null && !sender.hasPermission(RequiredPermission)) {
            ChatUtil.sendNoPermissionMessage(sender, DisplayName, RequiredPermission);
            return;
        }

        onCommand(sender, args);
    }
    public void onCommand(CommandSender sender, String[] args) { }
    public void onConfirm(CommandSender sender, String[] args) { }
    public void onResponse(CommandSender sender, String response) { }

    // must implement permission check
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }

    // todo: implement getMenuButtons
}
