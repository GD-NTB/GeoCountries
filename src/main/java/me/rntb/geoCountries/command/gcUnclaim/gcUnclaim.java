package me.rntb.geoCountries.command.gcUnclaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcUnclaim extends GeoCommand {

    public gcUnclaim(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Unclaims your country's chunks.";
        this.childCommands.put("one", new gcUnclaimOne(this, "one", "/gc unclaim one", "gc.unclaim.one", ItemUtil.getSkull(ItemUtil.Skull.RED_ONE)));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("one").onCommand(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).position == Position.LEADER;
    }
}
