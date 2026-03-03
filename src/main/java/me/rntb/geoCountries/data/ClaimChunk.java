package me.rntb.geoCountries.data;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.World;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimChunk extends DataCollection {

    public static String filePath;
    public static String displayName;

    // list of all claimchunks existing
    public static ArrayList<ClaimChunk> all = null;

    private static final Map<Long, ClaimChunk> byKey = new HashMap<>();
    public static ClaimChunk get(Long key) {
        return byKey.get(key);
    }

    public static void init() {
        filePath = "data/claimchunks";
        displayName = "ClaimChunk";

        all = readFromFile(filePath, displayName, new TypeToken<ArrayList<ClaimChunk>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(filePath));
            return;
        }

        // reset and populate hashmaps
        byKey.clear();
        for (ClaimChunk claimChunk : all) {
            byKey.put(claimChunk.key, claimChunk);
        }

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Loaded " + all.size() + " ClaimChunks");
    }

    public static void save() {
        writeToFile(ClaimChunk.filePath, ClaimChunk.displayName, all);

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
        add(this, all, displayName);
        byKey.put(key, this);

        this.timeCreated = System.currentTimeMillis();
    }

    public void deregister() {
        byKey.remove(key);

        // delete any associated applications
        // --

        delete(this, all, displayName);
    }

    // ---

    @SerializedName(value = "k", alternate = "key")
    private final long key;
    public long getKey() {
        return key;
    }

    @SerializedName(value = "w", alternate = "world")
    private final UUID world;
    public UUID getWorldUUID() {
        return world;
    }
    public World getWorld() {
        return world == null ? null : GeoCountries.server.getWorld(world);
    }
    public net.pl3x.map.core.world.World getPl3xMapWorld() {
        return Pl3xMapIntegration.api.getWorldRegistry().get(getWorld().getName());
    }

    private final int x;
    public int getX() {
        return x;
    }
    private final int z;
    public int getZ() {
        return z;
    }

    @SerializedName(value = "o", alternate = "owner")
    private final UUID owner;
    public UUID getOwner() {
        return owner;
    }

    @SerializedName(value = "t", alternate = "timeCreated")
    public long timeCreated = 0;
    public String timeCreatedAsString() {
        Instant instant = Instant.ofEpochMilli(timeCreated);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }

    public ClaimChunk(long key, UUID world, int x, int z, UUID owner) {
        this.key = key;
        this.world = world;
        this.x = x;
        this.z = z;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ClaimChunk(world=%s, x=%d, z=%d, owner=%s)"
                .formatted(this.world, this.x, this.z, this.owner);
    }
}
