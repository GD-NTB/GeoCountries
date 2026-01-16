package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcConfig extends SubCommand {

    public gcConfig(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages the plugin config.";
        this.HelpPage   = """
                          §f/gc config [...]: §aManages the plugin config file at config.yml.
                          §f> reload: §aReloads the config and updates the plugin's state.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc config
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
            case "reload":
                gcConfigReload.doCommand(sender, subArgs);
                return;
            // gc config [xxx]
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]"""
                                                     .formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc config 1
            case 1 -> Stream.of("reload").filter(x -> sender.hasPermission("gc.config." + x)).toList();
            default -> List.of();
        };
    }
}
