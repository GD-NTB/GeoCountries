package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.*;

public class ClaimChunk extends DataCollection {

    public static final String FILE_PATH = "data/claimchunks";
    public static final String DISPLAY_NAME = "ClaimChunk";

    // list of all claimchunks existing
    private static ArrayList<ClaimChunk> all = null;
    public static ArrayList<ClaimChunk> getAll() {
        return all;
    }

    private static final Map<Long, ClaimChunk> byKey = new HashMap<>();
    public static ClaimChunk get(Long key) {
        return byKey.get(key);
    }
    public static ClaimChunk get(int x, int z) {
        return get(packToKey(x, z));
    }
    public static ClaimChunk get(Chunk bukkitChunk) {
        return get(bukkitChunk.getChunkKey());
    }

    private static final Map<UUID, List<ClaimChunk>> byCountry = new HashMap<>();
    public static List<ClaimChunk> get(Country country) {
        if (country == null)
            return null;
        return byCountry.get(country.getUUID());
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
        byCountry.clear();
        for (ClaimChunk claimChunk : all) {
            // add to byKey
            byKey.put(claimChunk.key, claimChunk);
            // add to byCountry
            byCountry.computeIfAbsent(claimChunk.owner, v -> new ArrayList<>()).add(claimChunk);

            // unpack key into coordinates
            int[] coords = unpackFromKey(claimChunk.key);
            claimChunk.x = coords[0];
            claimChunk.z = coords[1];
        }

        if (ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(ClaimChunk.FILE_PATH, ClaimChunk.DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static int purge() {
        IntegrationManager.clearAll();
        int count = 0;
        for (ClaimChunk c : new ArrayList<>(all)) {
            c.deregister(false);
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, DISPLAY_NAME);

        // add to byKey
        byKey.put(key, this);
        // add to byCountry
        byCountry.computeIfAbsent(owner, v -> new ArrayList<>()).add(this);

        // update maps
        IntegrationManager.onClaim(getOwnerObject(), getX(), getZ());
    }

    public void deregister(boolean updateMaps) {
        if (updateMaps)
            IntegrationManager.onUnclaim(getOwnerObject(), getX(), getZ());

        // remove from byKey
        byKey.remove(key);
        // remove from byCountry
        ClaimChunk.byCountry.computeIfPresent(getOwner(),
                (k, v) -> { v.remove(this); return v.isEmpty() ? null : v; });

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

    // not serialised
    private int x;
    public int getX() {
        return x;
    }

    // not serialised
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
    public Country getOwnerObject() {
        return Country.get(owner);
    }

    public ClaimChunk(long key, UUID world, UUID owner) {
        this.key = key;
        int[] xz = unpackFromKey(key);
        this.x = xz[0];
        this.z = xz[1];

        this.world = world;
        this.owner = owner;
    }

    public ClaimChunk(int x, int z, UUID world, UUID owner) {
        this.x = x;
        this.z = z;
        this.key = packToKey(x, z);

        this.world = world;
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
