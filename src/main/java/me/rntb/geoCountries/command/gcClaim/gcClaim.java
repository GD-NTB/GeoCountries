package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gcUnclaim.gcUnclaim;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcClaim extends GeoCommand {

    public gcClaim(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages your country's claims and claims chunks.";
        this.childCommands = new LinkedHashMap<>() {{
            put("one", new gcClaimOne("one", "/gc claim one", "gc.claim.one", ItemUtil.getSkull(ItemUtil.Skull.ONE)));
            put("unclaim", new gcUnclaim("unclaim", "/gc claim unclaim", "gc.unclaim", ItemStack.of(Material.COPPER_SHOVEL)));
        }};
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
