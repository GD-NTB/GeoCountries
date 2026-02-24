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

public abstract class SubCommand {

    public String Name;
    public String DisplayName;
    public String RequiredPermission;
    public boolean ConsoleCanUse;
    public Material MenuItemMaterial;

    public String HelpString = "No help available."; // shown in /gc help
    public String getHelpPage() {
        StringBuilder sb = new StringBuilder("§f%s%s:§a %s\n"
                                             .formatted(DisplayName,
                                                        subSubCommands.isEmpty() ? "" : " [...]",
                                                        HelpString));
        for (SubSubCommand ssc : subSubCommands.values()) {
            sb.append("§f> ").append(ssc.Name).append(": §2").append(ssc.HelpString).append("\n");
        }
        return String.valueOf(sb);
    }
    public Map<String, SubSubCommand> subSubCommands = Collections.emptyMap();

    public SubCommand(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuItemMaterial) {
        this.Name = name;
        this.DisplayName = displayName;
        this.RequiredPermission = requiredPermission;
        this.ConsoleCanUse = consoleCanUse;
        this.MenuItemMaterial = menuItemMaterial;
    }

    public final void onCommandEntered(CommandSender sender, String[] args) {
        // if we are console and console can't use, escape
        if (!(sender instanceof Player) && !ConsoleCanUse) {
            ChatUtil.sendPrefixedPlayerOnlyErrorMessage(DisplayName);
            return;
        }

        //  if no permission, escape
        if (RequiredPermission != null && !sender.hasPermission(RequiredPermission)) {
            ChatUtil.sendNoPermissionMessage(sender, DisplayName, RequiredPermission);
            return;
        }

        onCommand(sender, args);
    }

    public void findAndExecuteSubCommand(CommandSender sender, String[] args, boolean onlyNeedBasePermission) {
        String mode = args[0].toLowerCase();
        SubSubCommand subSubCommand = subSubCommands.get(mode);
        if (subSubCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §c§f%s§c is not a valid command for §f%s§c!
                                                 Usage: §f%s [...]"""
                                                 .formatted(mode, DisplayName, DisplayName));
            return;
        }

        String permission = RequiredPermission + (onlyNeedBasePermission ? "" : "." + mode);
        if (!sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender, DisplayName + " " + mode, permission);
            return;
        }

        subSubCommand.onCommandEntered(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }
        findAndExecuteSubCommand(sender, args, true);
    }

    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length == 1)
            return allowedSubSubCommandsAsStrings(sender);

        SubSubCommand subSubCommand = this.subSubCommands.get(args[0]);
        if (subSubCommand == null || !sender.hasPermission(subSubCommand.RequiredPermission))
            return List.of();

        return subSubCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    // todo: implement getMenuButtons

    public List<SubSubCommand> allowedSubSubCommands(CommandSender sender) {
        return this.subSubCommands.values().stream()
                                           .filter(ssc -> sender.hasPermission(ssc.RequiredPermission)).toList();
    }
    public List<String> allowedSubSubCommandsAsStrings(CommandSender sender) {
        return this.subSubCommands.values().stream()
                                           .filter(ssc -> sender.hasPermission(ssc.RequiredPermission))
                                           .map(ssc -> ssc.Name)
                                           .sorted().toList();
    }

    public boolean isAdminCommand() {
        Permission parent = Bukkit.getPluginManager().getPermission("gc.group.admin");
        assert parent != null;
        return parent.getChildren().containsKey(this.RequiredPermission);
    }
}
