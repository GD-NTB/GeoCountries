package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcAdmin extends GeoCommand {

    public gcAdmin(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Useful admin commands for server management";
        this.childCommands = new LinkedHashMap<>() {{
            put("deletecountry", new gcAdminDeleteCountry("deletecountry", "/gc admin deletecountry", "gc.admin", Material.REDSTONE_BLOCK));
            put("setplayercountry", new gcAdminSetPlayerCountry("setplayercountry", "/gc admin setplayercountry", "gc.admin", Material.EMERALD_BLOCK));
            put("setplayerrank", new gcAdminSetPlayerRank("setplayerrank", "/gc admin setplayerrank", "gc.admin", Material.DIAMOND_BLOCK));
        }};
    }
}
