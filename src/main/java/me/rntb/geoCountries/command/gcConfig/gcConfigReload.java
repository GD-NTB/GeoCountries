package me.rntb.geoCountries.command.gcConfig;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcConfigReload extends GeoCommand {

    public gcConfigReload(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Reloads the config from disk to memory.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, "§aReloading config...");
        ConfigManager.reload();
        ChatUtil.sendPrefixedMessage(sender, "§aConfig reloaded!");
    }
}
