package me.rntb.geoCountries.command.gcUnclaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcUnclaim extends GeoCommand {

    public gcUnclaim(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Unclaims your country's chunks.");
        addChild(new gcUnclaimOne("one", "/gc unclaim one", ItemUtil.getSkull(ItemUtil.Skull.RED_ONE)));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            getChild("one").onCommandEntered(sender, args);
            return;
        }
        doChildCommand(sender, args);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
