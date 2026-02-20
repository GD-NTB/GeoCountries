package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcConfig extends SubCommand {

    public gcConfig(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages the plugin config.";
        this.HelpPage   = """
                          §f/gc config [...]: §aManages the plugin config file at config.yml.
                          §f> reload: §2Reloads the config and updates the plugin's state.""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("reload", gcConfigReload::onCommand)
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
        findAndExecuteSubCommand(sender, args, subCommands, true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc config [commands]
            case 1 -> subCommands.keySet().stream()
                                          .filter(x -> sender.hasPermission(this.RequiredPermission + "." + x))
                                          .toList();

            default -> List.of();
        };
    }
}
