package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcPlayer extends SubCommand {

    public gcPlayer(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages and views information about players.";
        this.HelpPage   = """
                          §f/gc player [...]§a: Manages and views information about players.
                          §f> info [username]: §aDisplays info about a particular player.""";
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aManages and views information about players.\n" +
                                                 "Usage: §f/gc player [...]");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc player info
            case "info":
                if (!sender.hasPermission("gc.player.info")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc player info", "gc.player.info");
                    return;
                }
                gcPlayerInfo.onCommand(sender, subArgs);
                return;

            // gc player [xxx]
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc player§c!\n" +
                                                     "Usage: §f/gc player [...]");
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            // /gc player 1
            case 1 -> Stream.of("info").filter(x -> sender.hasPermission("gc.player." + x)).toList();
            // /gc player [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc player info [players]
                    case "info" -> sender.hasPermission("info") ? PlayerData.AllAsUsernames() : List.of();
                    // /gc player [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
