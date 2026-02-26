package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcPlayer extends GeoCommand {

    public gcPlayer(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages and views information about players.";
        this.childCommands = new LinkedHashMap<>() {{
            put("info", new gcPlayerInfo("info", "/gc player info", "gc.player.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
            put("settings", new gcPlayerSettings("settings", "/gc player settings", "gc.player.settings", ItemStack.of(Material.WRITABLE_BOOK)));
        }};
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }
}
