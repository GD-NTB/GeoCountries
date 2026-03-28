package me.rntb.geoCountries.command;

import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.stream.Stream;

public abstract class GeoCommand {

    public static GeoCommand baseCommand;
    public static String adminPermissionGroup;

    public static Pair<GeoCommand, String[]> get(String input) {
        if (input == null || input.isEmpty())
            return Pair.of(null, new String[0]);

        String[] args = input.split(" ");

        // follow command tree starting with null
        if (!args[0].equals(baseCommand.getCommandString()))
            return Pair.of(null, args);

        GeoCommand command = baseCommand;
        int i = 1;
        while (i < args.length) {
            GeoCommand childCommand = command.childLookup.get(args[i]);
            if (childCommand == null)
                break;

            command = childCommand;
            i++;
        }

        String[] remainingPart = Arrays.copyOfRange(args, i, args.length);
        return Pair.of(command, remainingPart);
    }

    // todo: getters and setters
    public GeoCommand parentCommand;

    public String name;
    public String permission;
    public ItemStack menuButtonItem; // ItemStack.of(Material.DEBUG_STICK) gives skull with player's skin

    public String helpString = "No help available."; // shown in /gc help
    public final String getHelpPage(CommandSender sender) {
        boolean hasPermission = sender.hasPermission(permission);
        if (!hasPermission)
            return null;

        StringBuilder helpPage = new StringBuilder("%s/%s: §7%s"
                                             .formatted(isAdminCommand() ? "§e" : "§2",
                                                        getUsageString(),
                                                        helpString));

        List<GeoCommand> commands = allowedChildCommands(sender);
        if (!commands.isEmpty()) { // skip if no subcommands
            helpPage.append("\n");

            int i = 0;
            for (GeoCommand childCommand : commands) {
                helpPage.append("§f> %s%s%s: §f%s"
                          .formatted(childCommand.isAdminCommand() ? "§e" : "§a",
                                     childCommand.name,
                                     childCommand.children.isEmpty() ? "" : " [...]",
                                     childCommand.helpString));
                if (i != commands.size() - 1) // prevent last newline
                    helpPage.append("\n");

                i++;
            }
        }

        return String.valueOf(helpPage);
    }

    public final String getCommandString() {
        if (parentCommand == null)
            return name;
        return parentCommand.getCommandString() + " " + name; // holy moly
    }

    public final String getUsageString() {
        return getCommandString() + (children.isEmpty() ? "" : " [...]");
    }

    public Map<String, GeoCommand> childLookup = new HashMap<>();

    private final LinkedHashMap<String, GeoCommand> children = new LinkedHashMap<>();
    public void addChild(GeoCommand child) {
        child.parentCommand = this;

        children.put(child.name, child);

        childLookup.put(child.name, child);
        for (String alias : child.aliases) {
            childLookup.put(alias, child);
        }
    }

    private final List<String> aliases = new ArrayList<>();
    public void addAlias(String alias) {
        aliases.add(alias);
        if (parentCommand != null)
            parentCommand.childLookup.put(alias, this);
    }

    // todo: rename constructor arguments in all command files
    public GeoCommand(String name, String permission, ItemStack menuButtonItem) {
        this.name = name;
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
            ChatUtil.sendNoPermissionMessage(sender, "/" + getCommandString(), permission);
            return;
        }

        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s"""
                                                 .formatted(helpString, getUsageString()));
            return;
        }
        doChildCommand(sender, args);
    }

    public final void doChildCommand(CommandSender sender, String[] args) {
        if (children.isEmpty())
            return;

        String childCommandName = args[0];
        GeoCommand childCommand = childLookup.get(childCommandName);
        if (childCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §c§f%s§c is not a valid command for §f%s§c!
                                                 Usage: §f%s"""
                                                 .formatted(childCommandName, "/" + getCommandString(), getUsageString()));
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            ChatUtil.sendNoPermissionMessage(sender,
                                             "/" + getCommandString() + " " + childCommandName,
                                             permission);
            return;
        }

        childCommand.onCommandEntered(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public static List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return List.of();

        GeoCommand command = baseCommand;
        int i = 0;
        while (i < args.length - 1) {
            String arg = args[i];

            GeoCommand next = command.childLookup.get(arg);
            if (next == null)
                break;

            if (next.permission != null && !sender.hasPermission(next.permission))
                return List.of();

            command = next;
            i++;
        }

        // remaining args for this command
        String[] remainingArgs = Arrays.copyOfRange(args, i, args.length);
        List<String> completions = new ArrayList<>(command.getTabCompletion(sender, remainingArgs));
        completions.removeIf(c -> !c.startsWith(args[args.length-1]));
        return completions;
    }

    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? allowedChildCommandsAsStrings(sender) : List.of();
    }

    public ItemStack[] getMenuButtons(CommandSender sender) {
        if (children.isEmpty())
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
                                                                childCommand.getCommandString(),
                                                                childCommand.isAdminCommand());
            else
                buttons[i] = MenuPage.createButton(childCommand.menuButtonItem,
                                                   StringUtil.sentenceCase(childCommand.name),
                                                   "§f" + childCommand.helpString,
                                                   childCommand.getCommandString(),
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
        if (!(sender instanceof Player) || children.isEmpty())
            return List.of();

        return children.sequencedValues().stream()
                                         .filter(c -> c.permission == null || sender.hasPermission(c.permission)).toList();
    }
    public final List<String> allowedChildCommandsAsStrings(CommandSender sender) {
        if (!(sender instanceof Player) || children.isEmpty())
            return List.of();

        return children.values().stream()
                                .filter(sc -> sc.permission == null || sender.hasPermission(sc.permission))
                                .flatMap(sc -> Stream.concat(Stream.of(sc.name), sc.aliases.stream()))
                                .sorted()
                                .toList();
    }

    public final boolean isAdminCommand() {
        if (permission == null)
            return false;

        Permission adminPermission = Bukkit.getPluginManager().getPermission(adminPermissionGroup);
        assert adminPermission != null;
        return adminPermission.getChildren().containsKey(permission);
    }
}
