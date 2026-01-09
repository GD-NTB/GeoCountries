package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SubCommandTemplate extends SubCommand {

    public SubCommandTemplate(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Description here.";
        this.HelpPage   = """
                          §f/gc [...] [...]§a: Longer description here.
                          §f> subcommand: §2(Countryless-only) §aDoes more things.""";
    }

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc [...]
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aDescription here.\n" +
                                                 "Usage: §f/gc [...]");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            default:
                return;
        }
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
