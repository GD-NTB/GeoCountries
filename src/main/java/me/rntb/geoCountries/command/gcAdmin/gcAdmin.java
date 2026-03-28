package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcAdmin extends GeoCommand {

    public gcAdmin(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Useful admin commands for server management";
        addChild(new gcAdminDeleteCountry("deletecountry", "gc.admin", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcAdminSetPlayerCountry("setplayercountry", "gc.admin", ItemStack.of(Material.FILLED_MAP)));
        addChild(new gcAdminSetPlayerPosition("setplayerposition", "gc.admin", ItemStack.of(Material.GOLD_INGOT)));
        addChild(new gcAdminDeleteFaction("deletefaction", "gc.admin", ItemStack.of(Material.FLINT_AND_STEEL)));
    }
}
