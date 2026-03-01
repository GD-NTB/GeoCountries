package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcAdmin extends GeoCommand {

    public gcAdmin(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Useful admin commands for server management";
        this.childCommands = new LinkedHashMap<>() {{
            put("deletecountry", new gcAdminDeleteCountry("deletecountry", "/gc admin deletecountry", "gc.admin", ItemStack.of(Material.REDSTONE_BLOCK)));
            put("setplayercountry", new gcAdminSetPlayerCountry("setplayercountry", "/gc admin setplayercountry", "gc.admin", ItemStack.of(Material.EMERALD_BLOCK)));
            put("setplayerposition", new gcAdminSetPlayerPosition("setplayerposition", "/gc admin setplayerposition", "gc.admin", ItemStack.of(Material.DIAMOND_BLOCK)));
        }};
    }
}
