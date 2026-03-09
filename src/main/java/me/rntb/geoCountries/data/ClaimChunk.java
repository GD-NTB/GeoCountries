package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimChunk extends DataCollection {

    public static String FILE_PATH = "data/claimchunks";
    public static String DISPLAY_NAME = "ClaimChunk";

    // list of all claimchunks existing
    public static ArrayList<ClaimChunk> all = null;

    private static final Map<Long, ClaimChunk> byKey = new HashMap<>();
    public static ClaimChunk get(Long key) {
        return byKey.get(key);
    }
    public static ClaimChunk get(int x, int z) {
        return get(packToKey(x, z));
    }

    public static Long packToKey(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32; // Chunk.getChunkKey(x, z)
    }
    public static int[] unpackFromKey(long key) {
        return new int[] { (int) (key & 0xFFFFFFFFL), (int) (key >>> 32) };
    }


    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<ClaimChunk>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(FILE_PATH));
            return;
        }

        // reset and populate hashmaps
        byKey.clear();
        for (ClaimChunk claimChunk : all) {
            byKey.put(claimChunk.key, claimChunk);

            // add claimchunk to country
            claimChunk.getOwnerCountry().getClaimChunks().add(claimChunk.key);

            // unpack key into coordinates
            int[] coords = unpackFromKey(claimChunk.key);
            claimChunk.x = coords[0];
            claimChunk.z = coords[1];
        }

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Loaded " + all.size() + " ClaimChunks");
    }

    public static void save() {
        writeToFile(ClaimChunk.FILE_PATH, ClaimChunk.DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Saved " + all.size() + " ClaimChunks");
    }

    // returns number of claimchunks purged
    public static int purge() {
        int count = 0;
        for (ClaimChunk c : new ArrayList<>(all)) {
            c.deregister();
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, DISPLAY_NAME);
        byKey.put(key, this);

        // add this claimchunk to country
        getOwnerCountry().getClaimChunks().add(key);

//        // update maps
//        Pl3xMapIntegration.addClaim(this);
    }

    public void deregister() {
        byKey.remove(key);

        // remove this claimchunk from country
        getOwnerCountry().getClaimChunks().remove(key);

        // delete any associated applications
        // --

//        // update maps
//        Pl3xMapIntegration.clearClaim(this);

        delete(this, all, DISPLAY_NAME);
    }

    // ---

    @Expose
    @SerializedName(value = "k", alternate = "key")
    private final long key;
    public long getKey() {
        return key;
    }

    @Expose
    @SerializedName(value = "w", alternate = "world")
    private final UUID world;
    public UUID getWorld() {
        return world;
    }
    public World getBukkitWorld() {
        return world == null ? null : GeoCountries.server.getWorld(world);
    }
    public net.pl3x.map.core.world.World getPl3xMapWorld() {
        if (!IntegrationState.isPl3xMapEnabled)
            return null;
        return Pl3xMapIntegration.api.getWorldRegistry().get(getBukkitWorld().getName());
    }

    @Expose(serialize = false, deserialize = false)
    private int x;
    public int getX() {
        return x;
    }

    @Expose(serialize = false, deserialize = false)
    private int z;
    public int getZ() {
        return z;
    }

    @Expose
    @SerializedName(value = "o", alternate = "owner")
    private final UUID owner;
    public UUID getOwner() {
        return owner;
    }
    public Country getOwnerCountry() {
        return Country.get(owner);
    }

    public ClaimChunk(long key, UUID world, int x, int z, UUID owner) {
        this.key = key;
        this.world = world;
        this.x = x;
        this.z = z;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        ClaimChunk other = (ClaimChunk) otherObject;

        return key == other.key;
    }

    @Override
    public String toString() {
        return "ClaimChunk(world=%s, x=%d, z=%d, owner=%s)"
               .formatted(world, x, z, owner);
    }
}
