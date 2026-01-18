package me.rntb.geoCountries.command.debug;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class gcDebug extends SubCommand {

    public gcDebug(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Debug commands for development.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful debug commands for plugin development.
                          §f> createcountry [name]: §aCreates a test country.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /gc debug
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
            // gc debug createcountry
            case "createcountry":
                gcDebugCreateCountry.onCommand(sender, subArgs);
                return;

            // gc debug [xxx]
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
            // /gc debug 1
            case 1 -> sender.hasPermission("gc.debug") ? List.of("createcountry") : List.of();
            default -> List.of();
        };
    }
}
