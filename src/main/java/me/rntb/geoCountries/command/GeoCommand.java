package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.MenuPage;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.*;

public abstract class GeoCommand {
    
    public static GeoCommand baseCommand;
    
    public String name;
    public String displayName;
    public String permission;
    public Material menuButtonItem;

    public String helpString = "No help available."; // shown in /gc help
    public String getHelpPage() {
        StringBuilder sb = new StringBuilder("§f%s%s:§a %s\n"
                                             .formatted(displayName,
                                                        childCommands.isEmpty() ? "" : " [...]",
                                                        helpString));
        for (GeoCommand childCommand : childCommands.values()) {
            sb.append("§f> %s: §2%s\n"
                      .formatted(childCommand.name, childCommand.helpString));
        }
        return String.valueOf(sb);
    }

    public LinkedHashMap<String, GeoCommand> childCommands = new LinkedHashMap<>();
    // todo: implement childCommandsAliases

    public GeoCommand(String name, String displayName, String permission, Material menuButtonItem) {
        this.name = name;
        this.displayName = displayName;
        this.permission = permission;
        this.menuButtonItem = menuButtonItem;
    }

    public final void onCommandEntered(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendPrefixedLogErrorMessage("§cRunning commands via the console is not supported!");
            return;
        }

        //  if no permission, escape
        if (permission != null && !sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender, displayName, permission);
            return;
        }

        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // todo: this is probably formatted wrong...
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.helpString, this.displayName));
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }

    public final void findAndExecuteChildCommand(CommandSender sender, String[] args) {
        String mode = args[0].toLowerCase();
        GeoCommand childCommand = childCommands.get(mode);
        if (childCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §c§f%s§c is not a valid command for §f%s§c!
                                                 Usage: §f%s [...]"""
                                                 .formatted(mode, displayName, displayName));
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender, displayName + " " + mode, permission);
            return;
        }

        childCommand.onCommandEntered(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || childCommands.isEmpty())
            return List.of();

        if (args.length == 1)
            return allowedChildCommandsAsStrings(sender);

        GeoCommand command = childCommands.get(args[0]);
        if (command == null || (command.permission != null && !sender.hasPermission(command.permission)))
            return List.of();

        return command.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public ItemStack[] getMenuButtons(Player player) {
        int childCommandsCount = childCommands.size();
        if (childCommandsCount == 0)
            return new ItemStack[] { };

        List<GeoCommand> childCommandsList = new ArrayList<>(childCommands.sequencedValues());

        ItemStack[] buttons = new ItemStack[childCommandsCount];
        int i = 0;
        for (GeoCommand childCommand : childCommandsList) {
            if (childCommand.menuButtonItem == null)
                continue;

            String titleColour = childCommand.isAdminCommand() ? "§6" : "§a";
            buttons[i] = MenuPage.createButton(childCommand.menuButtonItem,
                                               titleColour + StringUtil.sentenceCase(childCommand.name),
                                               "§f" + childCommand.helpString,
                                               childCommand.displayName,
                                               player);

            i++;
        }

        return buttons;
    }

    public final List<GeoCommand> allowedChildCommands(CommandSender sender) {
        return this.childCommands.values().stream()
                                          .filter(cc -> cc.permission == null || sender.hasPermission(cc.permission)).toList();
    }
    public final List<String> allowedChildCommandsAsStrings(CommandSender sender) {
        return this.childCommands.values().stream()
                                          .filter(cc -> cc.permission == null || sender.hasPermission(cc.permission))
                                          .map(cc -> cc.name)
                                          .sorted().toList();
    }

    public final boolean isAdminCommand() {
        if (permission == null)
            return false;

        Permission adminPermission = Bukkit.getPluginManager().getPermission("gc.group.admin");
        assert adminPermission != null;
        return adminPermission.getChildren().containsKey(this.permission);
    }
}
