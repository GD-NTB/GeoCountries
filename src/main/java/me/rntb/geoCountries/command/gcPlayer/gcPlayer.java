package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcPlayer extends SubCommand {

    public gcPlayer(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages and views information about players.";
        this.HelpPage   = """
                          §f/gc player [...]: §aManages and views information about players.
                          §f> info [username]: §aDisplays info about a particular player.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }

        String mode = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc player info
            case "info":
                if (!sender.hasPermission("gc.player.info")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc player info", "gc.player.info");
                    return;
                }
                gcPlayerInfo.onCommand(sender, subArgs);
                return;

            // gc player [xxx]
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]""".formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc player 1
            case 1 -> Stream.of("info").filter(x -> sender.hasPermission("gc.citizenship." + x)).toList();
            // /gc player [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc player info [players]
                    case "info" -> sender.hasPermission("info") ? PlayerProfile.allAsUsernames(true) : List.of();
                    // /gc player [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
