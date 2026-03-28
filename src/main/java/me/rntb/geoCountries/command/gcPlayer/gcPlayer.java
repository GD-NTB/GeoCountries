package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcPlayer extends GeoCommand {

    public gcPlayer(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Manages and views info about players.");
        addChild(new gcPlayerInfo("info", "gc.player.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        addChild(new gcPlayerSettings("settings", "gc.player.settings", ItemStack.of(Material.WRITABLE_BOOK)));
        addAlias("p");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            getChild("info").onCommandEntered(sender, args);
            return;
        }
        doChildCommand(sender, args);
    }
}
