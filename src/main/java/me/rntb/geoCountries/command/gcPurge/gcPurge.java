package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcPurge extends GeoCommand {

    public gcPurge(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges (deletes) plugin data - should be used very rarely!";
        this.childCommands.put("all", new gcPurgeAll(this, "all", "/gc purge all", "gc.purge", ItemStack.of(Material.TNT)));
        this.childCommands.put("country", new gcPurgeCountry(this, "country", "/gc purge country", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("playerprofile", new gcPurgePlayerProfile(this, "playerprofile", "/gc purge playerprofile", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("username", new gcPurgeUsername(this, "username", "/gc purge username", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("uuid", new gcPurgeUUID(this, "uuid", "/gc purge uuid", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("citizenshipapplication", new gcPurgeCitizenshipApplication(this, "citizenshipapplication", "/gc purge citizenshipapplication", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("claimchunk", new gcPurgeClaimChunk(this, "claimchunk", "/gc purge claimchunk", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("faction", new gcPurgeFaction(this, "faction", "/gc purge faction", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
    }
}
