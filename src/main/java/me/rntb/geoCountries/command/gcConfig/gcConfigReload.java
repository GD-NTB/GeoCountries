package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.manager.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcConfigReload {

    public static void doCommand(CommandSender sender, String[] args) {
        ChatUtil.SendPrefixedMessage(sender, "§aReloading config...");
        ConfigManager.Reload();
        ChatUtil.SendPrefixedMessage(sender, "§aConfig reloaded!");
    }
}
