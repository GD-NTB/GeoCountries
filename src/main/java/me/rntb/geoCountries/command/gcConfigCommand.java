package me.rntb.geoCountries.command;

import me.rntb.geoCountries.manager.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class gcConfigCommand extends SubCommand {

    public gcConfigCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages the plugin config.";
        this.HelpPage   = """
                          §f/gc config [...]§a: Manages the plugin config file at config.yml.
                          §f> reload: §aReloads the config and updates the plugin's state.""";
    }

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
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
                gcConfigReload(sender, subArgs);
                return;
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc config§c!\n" +
                                                     "Usage: §f/gc config [...]");
                return;
        }
    }

    // ---------- /gc config reload ----------
    private void gcConfigReload(@NotNull CommandSender sender, @NotNull String[] args) {
        ChatUtil.SendPrefixedMessage(sender, "§aReloading config...");
        ConfigManager.Reload();
        ChatUtil.SendPrefixedMessage(sender, "§aConfig reloaded!");
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc config 1
        if (args.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
