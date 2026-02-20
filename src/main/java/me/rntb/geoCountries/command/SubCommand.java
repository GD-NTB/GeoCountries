package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class SubCommand {

    public String Name;
    public String DisplayName;
    public String RequiredPermission;
    public boolean ConsoleCanUse;
    public Material MenuItemMaterial;
    
    public String HelpString = ""; // shown in /gc help
    public String HelpPage = ""; // shown in /gc help [...]

    public Map<String, BiConsumer<CommandSender, String[]>> subSubCommands = Collections.emptyMap();

    public SubCommand(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuItemMaterial) {
        this.Name = name;
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
        this.ConsoleCanUse = consoleCanUse;
        this.MenuItemMaterial = menuItemMaterial;
    }

    public void onCommandEntered(CommandSender sender, String[] args) {
        // if we are console and console can't use, escape
        if (!(sender instanceof Player) && !this.ConsoleCanUse) {
            ChatUtil.sendPrefixedPlayerOnlyErrorMessage(this.DisplayName);
            return;
        }

        // if we are player and permission is needed and dont have permission, escape
        if (sender instanceof Player && RequiredPermission != null && !sender.hasPermission(RequiredPermission)) {
            ChatUtil.sendNoPermissionMessage(sender, this.DisplayName, this.RequiredPermission);
            return;
        }

        // else onCommand
        onCommand(sender, args);
    }

    public void findAndExecuteSubCommand(CommandSender sender, String[] args, Map<String, BiConsumer<CommandSender, String[]>> subCommands, boolean onlyNeedBasePermission) {
        String mode = args[0].toLowerCase();
        BiConsumer<CommandSender, String[]> method = subCommands.get(mode);
        if (method != null) {
            String permission = this.RequiredPermission + (onlyNeedBasePermission ? "" : "." + mode);
            if (!sender.hasPermission(permission)) {
                ChatUtil.sendNoPermissionMessage(sender, this.DisplayName + " " + mode, permission);
                return;
            }
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            method.accept(sender, subArgs);
        }
        else {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §c§f%s§c is not a valid command for §f%s§c!
                                                 Usage: §f%s [...]"""
                                                 .formatted(mode, this.DisplayName, this.DisplayName));
        }
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    public abstract List<String> getTabCompletion(CommandSender sender,  String[] args);

    public boolean isAdminCommand() {
        Permission parent = Bukkit.getPluginManager().getPermission("gc.group.admin");
        if (parent == null)
            return false; // should never trigger

        return parent.getChildren().containsKey(this.RequiredPermission);
    }
}
