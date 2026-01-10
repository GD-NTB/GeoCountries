package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcConfig extends SubCommand {

    public gcConfig(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages the plugin config.";
        this.HelpPage   = """
                          §f/gc config [...]§a: Manages the plugin config file at config.yml.
                          §f> reload: §aReloads the config and updates the plugin's state.""";
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc [...]
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aManages and reloads the config.\n" +
                                                  "§f/gc config [...]");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            case "reload":
                if (!sender.hasPermission("gc.config.reload")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc config reload", "gc.config.reload");
                    return;
                }
                gcConfigReload.doCommand(sender, subArgs);
                return;
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc config§c!\n" +
                                                     "Usage: §f/gc config [...]");
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            // /gc config 1
            case 1 -> Stream.of("reload").filter(x -> sender.hasPermission("gc.config." + x)).toList();
            default -> List.of();
        };
    }
}
