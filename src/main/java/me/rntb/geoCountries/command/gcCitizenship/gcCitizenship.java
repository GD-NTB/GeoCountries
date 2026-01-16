package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcCitizenship extends SubCommand {

    public gcCitizenship(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages your citizenship and your country's citizens.";
        this.HelpPage   = """
                          §f/gc citizenship [...]§a: Manages your citizenship and your country's citizens.
                          §f> apply: §2(Countryless-only) §Applies for citizenship to a country.
                          §f> accept: §2(Leader-only) §Accepts a citizenship application to your country.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc citizenship
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
            case "apply":
                if (!sender.hasPermission("gc.citizenship.apply")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship apply", "gc.citizenship.apply");
                    return;
                }
                gcCitizenshipApply.doCommand(sender, subArgs);
                return;
            // gc citizenship [xxx]
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
            // /gc citizen 1
            case 1 -> Stream.of("apply", "accept").filter(x -> sender.hasPermission("gc.citizenship." + x)).toList();
            // gc citizen [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc citizenship apply [countries]
                    case "apply" -> sender.hasPermission("gc.citizenship.apply") ? Country.allAsNames(true) : List.of();
                    // /gc citizenship accept [countries]
                    case "accept" -> sender.hasPermission("gc.citizenship.accept") ? Country.allAsNames(true) : List.of();
                    // /gc citizenship [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
