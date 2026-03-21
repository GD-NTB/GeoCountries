package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcAdmin extends GeoCommand {

    public gcAdmin(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Useful admin commands for server management";
        this.childCommands.put("deletecountry", new gcAdminDeleteCountry(this, "deletecountry", "/gc admin deletecountry", "gc.admin", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("setplayercountry", new gcAdminSetPlayerCountry(this, "setplayercountry", "/gc admin setplayercountry", "gc.admin", ItemStack.of(Material.FILLED_MAP)));
        this.childCommands.put("setplayerposition", new gcAdminSetPlayerPosition(this, "setplayerposition", "/gc admin setplayerposition", "gc.admin", ItemStack.of(Material.GOLD_INGOT)));
        this.childCommands.put("deletefaction", new gcAdminDeleteFaction(this, "deletefaction", "/gc admin deletefaction", "gc.admin", ItemStack.of(Material.FLINT_AND_STEEL)));
    }
}
