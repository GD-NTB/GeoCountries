package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class gcSubCommandTemplate extends SubCommand {

    public gcSubCommandTemplate(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Description here.";
        this.HelpPage   = """
                          §f/gc [...] [...]: §aLonger description here.
                          §f> subcommand: §2(Countryless-only) §aDoes more things.""";
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        // /gc [...]
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 1Usage: §f%s [...]""".formatted(this.HelpString, this.DisplayName));
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]""".formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
