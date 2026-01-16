package me.rntb.geoCountries.command;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenship;
import me.rntb.geoCountries.command.gcConfig.gcConfig;
import me.rntb.geoCountries.types.Confirmation;
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
        registerSubCommand("citizenship", new gcCitizenship("/gc citizenship", "gc.citizenship", false));
    }

    public static void registerSubCommand(String name, SubCommand subCommand) {
        gcSubCommands.put(name, subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)  {
        // do the command
        if (args.length == 0)
            doCommandNoArgs(sender); // /gc
        else
            doCommandArgs(sender, args); // /gc [...]

        return true;
    }

    private void doCommandNoArgs(@NotNull CommandSender sender) {
        // perms need checking if we are player
        if (sender instanceof Player && !sender.hasPermission("gc")) {
            ChatUtil.sendNoPermissionMessage(sender, "/gc", "gc");
            return;
        }

        ChatUtil.sendPrefixedMessage(sender, """
                                             §aThis server is running §f%s§a, a plugin developed by §frNTB§a
                                             Do §f/gc help§a for a list of commands!"""
                                             .formatted(GeoCountries.PluginNameAndVersion));
    }

    private void doCommandArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        // find subcommand
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = gcSubCommands.get(subCommandName);
        // subcommand doesnt exist
        if (subCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cThe command §f/gc %s§c doesn't exist!"
                                                 .formatted(subCommandName));
            return;
        }

        // if we are waiting for sender to confirm a command, but they sent a different command, cancel waiting
        UUID senderUuid = UuidUtil.GetUUIDOfCommandSender(sender);
        if (Confirmation.isWaiting(senderUuid)) {
            if (!(args.length == 1 && (args[0].equalsIgnoreCase("confirm") || args[0].equalsIgnoreCase("cancel")))) {
                Confirmation.stopWaiting(senderUuid, Confirmation.StopWaitingEvent.CANCELLED, true);
            }
        }

        // get subargs (the [...] in /gc subcommand [...])
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length); // get the [...] in /gc ... [...]

        // call subcommand.doCommandArgs(subArgs) with perms and console check
        subCommand.onCommandEntered(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        switch (args.length) {
            // /gc
            case 0: return List.of();
            // /gc [...]
            case 1: return GetAllowedSubCommandsAsStrings(player);
        };

        // /gc [subcommand] [...]
        // find subcommand
        SubCommand subCommand = gcSubCommands.get(args[0]);
        // if not found, escape
        if (subCommand == null)
            return List.of();

        // get tab completion of subcommand
        return subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
