package me.rntb.geoCountries.data;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;

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

    // list of all countries existing
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
        for (ClaimChunk country : all) {
            byKey.put(country.key, country);
        }

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Loaded " + all.size() + " ClaimChunks");
    }

    public static void save() {
        writeToFile(ClaimChunk.filePath, ClaimChunk.displayName, all);

        if (all != null && ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Saved " + all.size() + " ClaimChunks");
    }

    // returns number of countries purged
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
    public long key;

    public int x;
    public int z;

    @SerializedName(value = "o", alternate = "owner")
    public UUID owner;

    public long timeCreated = 0;
    public String timeCreatedAsString() {
        Instant instant = Instant.ofEpochMilli(timeCreated);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }

    public ClaimChunk(long key, int x, int z, UUID owner) {
        this.key = key;
        this.x = x;
        this.z = z;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ClaimChunk(x=%d, z=%d, owner=%s)"
                .formatted(this.x, this.z, this.owner);
    }
}
