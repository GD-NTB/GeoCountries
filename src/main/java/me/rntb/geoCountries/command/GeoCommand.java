package me.rntb.geoCountries.command;

import me.rntb.geoCountries.menu.MenuPage;
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
    public static String adminPermissionGroup;
    
    public String name;
    public String command;
    public static HashMap<String, GeoCommand> getByCommandString = new HashMap<>();
    public String permission;
    public ItemStack menuButtonItem; // ItemStack.of(Material.PLAYER_HEAD) gives skull with player's skin

    public String helpString = "No help available."; // shown in /gc help
    public String getHelpPage() {
        StringBuilder sb = new StringBuilder("§f%s%s:§a %s\n"
                                             .formatted(command,
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

    public GeoCommand(String name, String command, String permission, ItemStack menuButtonItem) {
        this.name = name;
        this.command = command;
        this.permission = permission;
        this.menuButtonItem = menuButtonItem;

        getByCommandString.put(command, this);
    }

    public final void onCommandEntered(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendPrefixedLogErrorMessage("§cRunning commands via the console is not supported!");
            return;
        }

        //  if no permission, escape
        if (permission != null && !sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender, command, permission);
            return;
        }

        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.helpString, this.command));
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
                                                 .formatted(mode, command, command));
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender, command + " " + mode, permission);
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

    public ItemStack[] getMenuButtons(CommandSender sender) {
        if (childCommands.isEmpty())
            return null;

        List<GeoCommand> allowedChildCommands = new ArrayList<>(allowedChildCommands(sender));
        allowedChildCommands.removeIf(c -> c.menuButtonItem == null || !c.isVisibleOnMenu(sender));

        ItemStack[] buttons = new ItemStack[allowedChildCommands.size()];
        int i = 0;
        for (GeoCommand childCommand : allowedChildCommands) {
            if (childCommand.menuButtonItem.getType() == Material.DEBUG_STICK)
                buttons[i] = MenuPage.createButtonOfPlayerSkull((Player) sender,
                                                                StringUtil.sentenceCase(childCommand.name),
                                                                "§f" + childCommand.helpString,
                                                                childCommand.command,
                                                                childCommand.isAdminCommand());
            else
                buttons[i] = MenuPage.createButton(childCommand.menuButtonItem,
                                                   StringUtil.sentenceCase(childCommand.name),
                                                   "§f" + childCommand.helpString,
                                                   childCommand.command,
                                                   childCommand.isAdminCommand());

            i++;
        }

        return buttons;
    }

    // permission check not needed as based on allowedChildCommands
    public boolean isVisibleOnMenu(CommandSender sender) {
        return true;
    }

    public final List<GeoCommand> allowedChildCommands(CommandSender sender) {
        if (!(sender instanceof Player) || childCommands.isEmpty())
            return List.of();

        return this.childCommands.sequencedValues().stream()
                                                   .filter(c -> c.permission == null || sender.hasPermission(c.permission)).toList();
    }
    public final List<String> allowedChildCommandsAsStrings(CommandSender sender) {
        if (!(sender instanceof Player) || childCommands.isEmpty())
            return List.of();

        return this.childCommands.sequencedValues().stream()
                                                   .filter(c -> c.permission == null || sender.hasPermission(c.permission))
                                                   .map(c -> c.name)
                                                   .sorted().toList();
    }

    public final boolean isAdminCommand() {
        if (permission == null)
            return false;

        Permission adminPermission = Bukkit.getPluginManager().getPermission(adminPermissionGroup);
        assert adminPermission != null;
        return adminPermission.getChildren().containsKey(this.permission);
    }
}
