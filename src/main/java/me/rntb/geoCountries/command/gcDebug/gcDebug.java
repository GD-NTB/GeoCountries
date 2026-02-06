package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcDebug extends SubCommand {

    public gcDebug(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Debug commands for development.";
        this.HelpPage   = """
                          §f/gc debug [...]: §aUseful debug commands for plugin development.
                          §f> createcountry [name]: §2Creates a test country.
                          §f> soundtest: §2Plays a sound effect.""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("createcountry", gcDebugCreateCountry::onCommand),
            Map.entry("soundtest", gcDebugSoundTest::onCommand)
    );

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }
        findAndExecuteSubCommand(sender, args, subCommands);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc debug [commands]
            case 1 -> subCommands.keySet().stream().toList();

            default -> List.of();
        };
    }
}
