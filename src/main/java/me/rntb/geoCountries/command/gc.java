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
import java.util.stream.Stream;

public class gc extends GeoCommand implements TabExecutor  {

    public gc(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "The base command for GeoCountries. Opens the plugin's visual GUI menu.";
        this.childCommands = new LinkedHashMap<>() {{
            put("country", new gcCountry("country", "/gc country", "gc.country", Material.FILLED_MAP));
            put("player", new gcPlayer("player", "/gc player", "gc.player", Material.PLAYER_HEAD));
            put("citizenship", new gcCitizenship("citizenship", "/gc citizenship", "gc.citizenship", Material.WRITABLE_BOOK));
            put("help", new gcHelp("help", "/gc help", "gc.help", Material.GRASS_BLOCK));
            put("admin", new gcAdmin("admin", "/gc admin", "gc.admin", Material.DIAMOND_BLOCK));
            put("debug", new gcDebug("debug", "/gc debug", "gc.debug", Material.REDSTONE));
            put("config", new gcConfig("config", "/gc config", "gc.config", Material.BOOK));
            put("save", new gcSave("save", "/gc save", "gc.save", Material.RED_BED));
            put("load", new gcLoad("load", "/gc load", "gc.load", Material.CARROT_ON_A_STICK));
            put("purge", new gcPurge("purge", "/gc purge", "gc.purge", Material.FLINT_AND_STEEL));
            put("dump", new gcDump("dump", "/gc dump", "gc.dump", Material.BAKED_POTATO));
            put("gui", new gcGui("gui", "/gc gui", "gc.gui", null));
            put("confirm", new gcConfirm("confirm", "/gc confirm", "gc.confirm", null));
            put("cancel", new gcCancel("cancel", "/gc cancel", "gc.cancel", null));
        }};

        GeoCommand.baseCommand = this;
    }

    public Map<String, String> childCommandsAliases = Map.ofEntries(
            Map.entry("c", "country"),
            Map.entry("p", "player"),
            Map.entry("citizen", "citizenship")
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)  {
        if (!sender.hasPermission("gc")) {
            ChatUtil.sendNoPermissionMessage(sender, "/gc", "gc");
            return true;
        }

        if (args.length == 0)
            childCommands.get("gui").onCommandEntered(sender, new String[] { });
        else
            onCommandArgs(sender, args);

        return true;
    }

    private void onCommandArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        // find childCommand
        String childCommandName = args[0].toLowerCase();
        // replace with alias if needed
        String childCommandNameAlias = childCommandsAliases.get(childCommandName);
        if (childCommandNameAlias != null)
            childCommandName = childCommandNameAlias;

        GeoCommand childCommand = childCommands.get(childCommandName);
        if (childCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cThe command §f/gc %s§c doesn't exist!"
                                                 .formatted(childCommandName));
            return;
        }

        // if we are waiting for sender to confirm a command, but they sent a different command, cancel waiting
        UUID senderUuid = UuidUtil.getUUIDOfCommandSender(sender);
        if (Confirmation.isWaiting(senderUuid))
            if (!(args.length == 1 && (args[0].equalsIgnoreCase("confirm") || args[0].equalsIgnoreCase("cancel"))))
                Confirmation.stopWaiting(senderUuid, Confirmation.StopWaitingEvent.CANCELLED, true);

        // get subargs (the [...] in /gc [childCommand] [...])
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        // do perms and console check then childCommand.onCommand
        childCommand.onCommandEntered(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !(sender instanceof Player player))
            return List.of();

        if (args.length == 1)
            return getTabCompletion(player, new String[]{ });

        String commandName = args[0];
        // convert alias to childCommand
        String childCommandNameAlias = this.childCommandsAliases.get(commandName);
        if (childCommandNameAlias != null)
            commandName = childCommandNameAlias;
        // find childCommand
        GeoCommand childCommand = childCommands.get(commandName);
        if (childCommand == null || !sender.hasPermission(childCommand.permission))
            return List.of();

        // get tab completion of childCommand
        return childCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return Stream.concat(childCommands.values().stream()
                                                   .filter(sc -> sender.hasPermission(sc.permission))
                                                   .map(sc -> sc.name),
                             childCommandsAliases.entrySet().stream()
                                                            .filter(sca -> sender.hasPermission(childCommands.get(sca.getValue()).permission))
                                                            .map(Map.Entry::getKey))
                     .sorted().toList();
    }
}
