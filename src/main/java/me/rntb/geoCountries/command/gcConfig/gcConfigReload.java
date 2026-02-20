package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcConfigReload extends SubSubCommand {

    public gcConfigReload(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, "§aReloading config...");
        ConfigManager.reload();
        ChatUtil.sendPrefixedMessage(sender, "§aConfig reloaded!");
    }
}
