package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcConfigReload {

    public static void doCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, "§aReloading config...");
        ConfigManager.reload();
        ChatUtil.sendPrefixedMessage(sender, "§aConfig reloaded!");
    }
}
