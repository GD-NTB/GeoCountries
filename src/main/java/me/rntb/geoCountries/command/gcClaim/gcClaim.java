package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gcUnclaim.gcUnclaim;
import me.rntb.geoCountries.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcClaim extends GeoCommand {

    public gcClaim(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Manages your country's claims.");
        addChild(new gcClaimOne("one", "gc.claim.one", ItemUtil.getSkull(ItemUtil.Skull.ONE)));
        addChild(new gcUnclaim("unclaim", "gc.unclaim", ItemStack.of(Material.COPPER_SHOVEL)));
        addChild(new gcClaimMap("map", "gc.claim.map", ItemStack.of(Material.FILLED_MAP)));
        addChild(new gcClaimInfo("info", "gc.claim.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            getChild("one").onCommandEntered(sender, args);
            return;
        }
        doChildCommand(sender, args);
    }
}
