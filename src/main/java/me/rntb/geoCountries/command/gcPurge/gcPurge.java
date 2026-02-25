package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcPurge extends GeoCommand {

    public gcPurge(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges (deletes) plugin data - should be used very rarely!";
        this.childCommands = new LinkedHashMap<>() {{
            put("citizenshipapplication", new gcPurgeCitizenshipApplication("citizenshipapplication", "/gc purge citizenshipapplication", "gc.purge", Material.FLINT_AND_STEEL));
            put("country", new gcPurgeCountry("country", "/gc purge country", "gc.purge", Material.FLINT_AND_STEEL));
            put("playerprofile", new gcPurgePlayerProfile("playerprofile", "/gc purge playerprofile", "gc.purge", Material.FLINT_AND_STEEL));
            put("uuid", new gcPurgeUUID("uuid", "/gc purge uuid", "gc.purge", Material.FLINT_AND_STEEL));
            put("username", new gcPurgeUsername("username", "/gc purge username", "gc.purge", Material.FLINT_AND_STEEL));
        }};
    }
}
