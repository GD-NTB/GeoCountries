package me.rntb.geoCountries.command;

import me.rntb.geoCountries.command.gcAdmin.gcAdmin;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenship;
import me.rntb.geoCountries.command.gcConfig.gcConfig;
import me.rntb.geoCountries.command.gcCountry.gcCountry;
import me.rntb.geoCountries.command.gcDebug.gcDebug;
import me.rntb.geoCountries.command.gcPlayer.gcPlayer;
import me.rntb.geoCountries.command.gcPurge.gcPurge;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class gc implements TabExecutor {

    public static Map<String, SubCommand> subCommands = Map.ofEntries(
            Map.entry("help", new gcHelp("help", "/gc help", "gc.help", true, Material.GRASS_BLOCK)),
            Map.entry("purge", new gcPurge("purge", "/gc purge", "gc.purge", true, Material.FLINT_AND_STEEL)),
            Map.entry("dump", new gcDump("dump", "/gc dump", "gc.dump", true, Material.BAKED_POTATO)),
            Map.entry("country", new gcCountry("country", "/gc country", "gc.country", false, Material.EMERALD)),
            Map.entry("player", new gcPlayer("player", "/gc player", "gc.player", false, Material.PLAYER_HEAD)),
            Map.entry("confirm", new gcConfirm("confirm", "/gc confirm", "gc.confirm", true, null)),
            Map.entry("cancel", new gcCancel("cancel", "/gc cancel", "gc.cancel", true, null)),
            Map.entry("save", new gcSave("save", "/gc save", "gc.save", true, Material.RED_BED)),
            Map.entry("config", new gcConfig("config", "/gc config", "gc.config", true, Material.GRINDSTONE)),
            Map.entry("citizenship", new gcCitizenship("citizenship", "/gc citizenship", "gc.citizenship", false, Material.WRITABLE_BOOK)),
            Map.entry("debug", new gcDebug("debug", "/gc debug", "gc.debug", true, Material.ANVIL)),
            Map.entry("admin", new gcAdmin("admin", "/gc admin", "gc.admin", true, Material.DIAMOND_BLOCK)),
            Map.entry("load", new gcLoad("load", "/gc load", "gc.load", true, Material.CARROT_ON_A_STICK)),
            Map.entry("gui", new gcGui("gui", "/gc gui", "gc.gui", false, null))
    );
    public static Map<String, String> subCommandsAliases = Map.ofEntries(
            Map.entry("c", "country"),
            Map.entry("p", "player"),
            Map.entry("citizen", "citizenship")
    );

    public static List<SubCommand> allowedSubCommands(CommandSender sender) {
        return subCommands.values().stream()
                                   .filter(sc -> sender.hasPermission(sc.RequiredPermission))
                                   .toList();
    }
    public static List<String> allowedSubCommandsAsStrings(CommandSender sender) {
        return subCommands.entrySet().stream()
                                     .filter(sc -> sender.hasPermission(sc.getValue().RequiredPermission))
                                     .map(Map.Entry::getKey)
                                     .sorted().toList();
    }
    public static List<String> subCommandsTabAutoCompleteList(CommandSender sender) {
        List<String> subCommandsStrings = new ArrayList<>(allowedSubCommandsAsStrings(sender));
        subCommandsStrings.addAll(subCommandsAliases.keySet());
        // yeah the list is gonna have been sorted twice, and what mate?
        return subCommandsStrings.stream()
                                 .sorted().toList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)  {
        if (args.length == 0)
            onCommandNoArgs(sender); // /gc
        else
            onCommandArgs(sender, args); // /gc [...]
        return true;
    }

    private void onCommandNoArgs(@NotNull CommandSender sender) {
        if (!sender.hasPermission("gc")) {
            ChatUtil.sendNoPermissionMessage(sender, "/gc", "gc");
            return;
        }
        // do /gc gui
        if (!sender.hasPermission("gc.gui")) {
            ChatUtil.sendNoPermissionMessage(sender, "/gc gui", "gc.gui");
            return;
        }
        subCommands.get("gui").onCommand(sender, new String[]{ });

//        ChatUtil.sendPrefixedMessage(sender, """
//                                             §aThis server is running §f%s§a, a plugin developed by §frNTB§a.
//                                             Do §f/gc help§a for a list of commands!"""
//                                             .formatted(GeoCountries.PluginNameAndVersion));
    }

    private void onCommandArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        // find subcommand
        String subCommandName = args[0].toLowerCase();
        // replace with alias if needed
        String subCommandNameAlias = subCommandsAliases.get(subCommandName);
        if (subCommandNameAlias != null)
            subCommandName = subCommandNameAlias;

        SubCommand subCommand = subCommands.get(subCommandName);
        if (subCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cThe command §f/gc %s§c doesn't exist!"
                                                 .formatted(subCommandName));
            return;
        }

        // if we are waiting for sender to confirm a command, but they sent a different command, cancel waiting
        UUID senderUuid = UuidUtil.getUUIDOfCommandSender(sender);
        if (Confirmation.isWaiting(senderUuid)) {
            if (!(args.length == 1 && (args[0].equalsIgnoreCase("confirm") || args[0].equalsIgnoreCase("cancel")))) {
                Confirmation.stopWaiting(senderUuid, Confirmation.StopWaitingEvent.CANCELLED, true);
            }
        }

        // get subargs (the [...] in /gc [subcommand] [...])
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        // do perms and console check then subcommand.onCommandArgs
        subCommand.onCommandEntered(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) // tab autocomplete disabled for console
            return List.of();

        return switch (args.length) {
            // /gc
            case 0 -> List.of();

            // /gc [...]
            case 1 -> subCommandsTabAutoCompleteList(player);

            // /gc [subcommand] [...]
            default -> {
                String commandName = args[0];
                // convert alias to subcommand
                String subCommandNameAlias = gc.subCommandsAliases.get(commandName);
                if (subCommandNameAlias != null)
                    commandName = subCommandNameAlias;
                // find subcommand
                SubCommand subCommand = subCommands.get(commandName);
                if (subCommand == null || !sender.hasPermission(subCommand.RequiredPermission))
                    yield List.of();

                // get tab completion of subcommand
                yield subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        };
    }
}
