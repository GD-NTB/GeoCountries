package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcAdmin extends SubCommand {

    public gcAdmin(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Useful admin commands for server management";
        this.subSubCommands = new LinkedHashMap<>() {{
                put("deletecountry", new gcAdminDeleteCountry("deletecountry", "/gc admin deletecountry", "gc.admin"));
                put("setplayercountry", new gcAdminSetPlayerCountry("setplayercountry", "/gc admin setplayercountry", "gc.admin"));
                put("setplayerrank", new gcAdminSetPlayerRank("setplayerrank", "/gc admin setplayerrank", "gc.admin"));
        }};
    }
}
