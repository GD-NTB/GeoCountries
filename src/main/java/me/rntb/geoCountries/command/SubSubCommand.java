package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.List;

// todo: SubCommand and SubSubCommand should inherit from a common base Command class?
public abstract class SubSubCommand {

    public String Name;
    public String DisplayName;
    public String RequiredPermission;
    public Material MenuItemMaterial;

    public String HelpString = "No help available."; // shown in /gc help [subcommand] [...]
    // todo: implement getHelpPage

    public SubSubCommand(String name, String displayName, String requiredPermission) {
        this.Name = name;
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
    }

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

    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }

    // todo: implement getMenuButtons

    public boolean isAdminCommand() {
        Permission adminPermission = Bukkit.getPluginManager().getPermission("gc.group.admin");
        assert adminPermission != null;
        return adminPermission.getChildren().containsKey(this.RequiredPermission);
    }
}
