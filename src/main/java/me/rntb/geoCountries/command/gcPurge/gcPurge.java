package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcPurge extends GeoCommand {

    public gcPurge(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Purges (deletes) plugin data.");
        addChild(new gcPurgeAll("all", "gc.purge", ItemStack.of(Material.TNT)));
        addChild(new gcPurgeCountry("country", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgePlayerProfile("playerprofile", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeUsername("username", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeUUID("uuid", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeCitizenshipApplication("citizenshipapplication", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeClaimChunk("claimchunk", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeFaction("faction", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcPurgeFactionInvite("factioninvite", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));

    }
}
