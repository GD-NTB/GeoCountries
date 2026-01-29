package me.rntb.geoCountries.command;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.command.gcAdmin.gcAdmin;
import me.rntb.geoCountries.command.gcDebug.gcDebug;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenship;
import me.rntb.geoCountries.command.gcConfig.gcConfig;
import me.rntb.geoCountries.command.gcCountry.gcCountry;
import me.rntb.geoCountries.command.gcPlayer.gcPlayer;
import me.rntb.geoCountries.command.gcPurge.gcPurge;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// all args for /gc are mapped to their respective subcommand through here
public class gc implements TabExecutor { // TabExecutor extends CommandExecutor

    public static Map<String, SubCommand> gcSubCommands = Map.ofEntries(
            Map.entry("help", new gcHelp("/gc help", "gc.help", true)),
            Map.entry("purge", new gcPurge("/gc purge", "gc.purge", true)),
            Map.entry("dump", new gcDump("/gc dump", "gc.dump", true)),
            Map.entry("country", new gcCountry("/gc country", "gc.country", true)),
            Map.entry("player", new gcPlayer("/gc player", "gc.player", true)),
            Map.entry("confirm", new gcConfirm("/gc confirm", "gc.confirm", true)),
            Map.entry("cancel", new gcCancel("/gc cancel", "gc.cancel", true)),
            Map.entry("save", new gcSave("/gc save", "gc.save", true)),
            Map.entry("config", new gcConfig("/gc config", "gc.config", true)),
            Map.entry("citizenship", new gcCitizenship("/gc citizenship", "gc.citizenship", false)),
            Map.entry("debug", new gcDebug("/gc debug", "gc.debug", false)),
            Map.entry("admin", new gcAdmin("/gc admin", "gc.admin", true)),
            Map.entry("load", new gcLoad("/gc load", "gc.load", true))
    );

    public static List<SubCommand> GetAllowedSubCommands(CommandSender sender) {
        return gcSubCommands.values().stream()
                                     .filter(sc -> sender.hasPermission(sc.RequiredPermission))
                                     .toList();
    }
    public static List<String> GetAllowedSubCommandsAsStrings(CommandSender sender) {
        return gcSubCommands.entrySet().stream()
                                       .filter(sc -> sender.hasPermission(sc.getValue().RequiredPermission))
                                       .map(Map.Entry::getKey)
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

        ChatUtil.sendPrefixedMessage(sender, """
                                             §aThis server is running §f%s§a, a plugin developed by §frNTB§a.
                                             Do §f/gc help§a for a list of commands!"""
                                             .formatted(GeoCountries.PluginNameAndVersion));
    }

    private void onCommandArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        // find subcommand
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = gcSubCommands.get(subCommandName);
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
        Player player = (Player) sender;
        return switch (args.length) {
            // /gc
            case 0 -> List.of();

            // /gc [...]
            case 1 -> GetAllowedSubCommandsAsStrings(player);

            // /gc [subcommand] [...]
            default -> {
                // find subcommand
                SubCommand subCommand = gcSubCommands.get(args[0]);
                if (subCommand == null || !sender.hasPermission(subCommand.RequiredPermission))
                    yield List.of();

                // get tab completion of subcommand
                yield subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        };
    }
}
