package me.rntb.geoCountries.command;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.command.gcConfig.gcConfig;
import me.rntb.geoCountries.command.gcCountry.gcCountry;
import me.rntb.geoCountries.command.gcPlayer.gcPlayer;
import me.rntb.geoCountries.command.gcPurge.gcPurge;
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

    public static Map<String, SubCommand> gcSubCommands = new HashMap<>();

    public static List<SubCommand> GetAllowedSubCommands(Player player) {
        return gcSubCommands.values().stream().filter(sc -> player.hasPermission(sc.RequiredPermission)).toList();
    }
    public static List<String> GetAllowedSubCommandsAsStrings(Player player) {
        return gcSubCommands.entrySet().stream().filter(sc -> player.hasPermission(sc.getValue().RequiredPermission))
                                                .map(Map.Entry::getKey).sorted().toList();
    }

    public static void registerSubCommands() {
        registerSubCommand("help", new gcHelp("/gc help", "gc.help", true));
        registerSubCommand("purge", new gcPurge("/gc purge", "gc.purge", true));
        registerSubCommand("dump", new gcDump("/gc dump", "gc.dump", true));
        registerSubCommand("country", new gcCountry("/gc country", "gc.country", true));
        registerSubCommand("player", new gcPlayer("/gc player", "gc.player", true));
        registerSubCommand("confirm", new gcConfirm("/gc confirm", "gc.confirm", true));
        registerSubCommand("cancel", new gcCancel("/gc cancel", "gc.cancel", true));
        registerSubCommand("save", new gcSave("/gc save", "gc.save", true));
        registerSubCommand("config", new gcConfig("/gc config", "gc.config", true));
    }

    public static void registerSubCommand(String name, SubCommand subCommand) {
        gcSubCommands.put(name, subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)  {
        // if we are waiting for sender to confirm a command, but they sent a different command, cancel waiting
        UUID senderUuid = UuidUtil.GetUUIDOfCommandSender(sender);
        if (gcConfirm.IsWaitingForSender(senderUuid)) {
            if (!(args.length == 1 && (args[0].equals("confirm") || args[0].equals("cancel")))) {
                ChatUtil.SendPrefixedMessage(sender, "§6Cancelled the command as you didn't type §f/gc confirm§6.");
                gcConfirm.StopWaitingForSender(senderUuid);
            }
        }

        // do the command
        if (args.length == 0) {
            // /gc
            doCommandNoArgs(sender);
        }
        else {
            // /gc [...]
            doCommandArgs(sender, args);
        }

        return true;
    }

    private void doCommandNoArgs(@NotNull CommandSender sender) {
        // perms need checking if we are player
        if (sender instanceof Player && !sender.hasPermission("gc")) {
            ChatUtil.SendNoPermissionMessage(sender, "/gc", "gc");
            return;
        }

        ChatUtil.SendPrefixedMessage(sender, String.format("§aThis server is running §f%s§a, a plugin developed by §frNTB§a.\n" +
                                                           "Do §f/gc help§a for a list of commands!",
                                                            GeoCountries.PluginNameAndVersion));
    }

    private void doCommandArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        // find subcommand
        String subCommandName = args[0];
        SubCommand subCommand = gcSubCommands.get(subCommandName);
        // subcommand doesnt exist
        if (subCommand == null) {
            ChatUtil.SendPrefixedMessage(sender, String.format("§cThe command §f/gc %s§c doesn't exist!",
                                                               subCommandName));
            return;
        }

        // get subargs (the [...] in /gc subcommand [...])
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length); // get the [...] in /gc ... [...]

        // call subcommand.doCommandArgs(subArgs) with perms and console check
        subCommand.onCommandEntered(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            // /gc
            case 0: return List.of();
            // /gc [...]
            case 1: return GetAllowedSubCommandsAsStrings((Player) sender);
        };

        // /gc [subcommand] [...]
        // find subcommand
        SubCommand subCommand = gcSubCommands.get(args[0]);
        // if not found, escape
        if (subCommand == null) {
            return List.of();
        }
        // get tab completion of subcommand
        return subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
