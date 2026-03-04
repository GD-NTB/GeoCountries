package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gcUnclaim.gcUnclaim;
import me.rntb.geoCountries.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcClaim extends GeoCommand {

    public gcClaim(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages your country's claims.";
        this.childCommands.put("one", new gcClaimOne(this, "one", "/gc claim one", "gc.claim.one", ItemUtil.getSkull(ItemUtil.Skull.ONE)));
        this.childCommands.put("map", new gcClaimMap(this, "map", "/gc claim map", "gc.claim.map", ItemStack.of(Material.FILLED_MAP)));
        this.childCommands.put("updatemapcolour", new gcClaimUpdateMapColour(this, "updatemapcolour", "/gc claim updatemapcolour", "gc.claim.updatemapcolour", ItemStack.of(Material.BELL)));
        this.childCommands.put("unclaim", new gcUnclaim(this, "unclaim", "/gc claim unclaim", "gc.unclaim", ItemStack.of(Material.COPPER_SHOVEL)));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("one").onCommand(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }
}
