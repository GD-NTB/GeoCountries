package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionInvite extends DataCollection {

    public static final String FILE_PATH = "data/factioninvites";
    public static final String DISPLAY_NAME = "FactionInvite";

    // list of all pending faction invites sent
    public static ArrayList<FactionInvite> all = null;
    public static final Map<UUID, FactionInvite> byUUID = new HashMap<>();
    public static final Map<UUID, ArrayList<FactionInvite>> byFromFaction = new HashMap<>();
    public static final Map<UUID, ArrayList<FactionInvite>> byToCountry = new HashMap<>();

    public static void init() {
        all = readFromFile(FactionInvite.FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<FactionInvite>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(FILE_PATH));
            return;
        }

        // reset and populate hashmaps
        byUUID.clear();
        byFromFaction.clear();
        byToCountry.clear();
        for (FactionInvite cApplication : all) {
            // add to byUUID
            byUUID.put(cApplication.uuid, cApplication);
            // add to byFromFaction
            byFromFaction.computeIfAbsent(cApplication.fromFaction, v -> new ArrayList<>()).add(cApplication);
            // add to byToCountry
            byToCountry.computeIfAbsent(cApplication.toCountry, v -> new ArrayList<>()).add(cApplication);
        }

        if (ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static int purge() {
        int count = 0;
        for (FactionInvite fi : new ArrayList<>(all)) {
            FactionInviteService.unsend(fi);
            count++;
        }
        return count;
    }

    // ---

    @Expose
    private final UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    // country that sent the invite
    @Expose
    private final UUID fromFaction;
    public UUID getFromFaction() {
        return fromFaction;
    }
    public Faction getFromFactionObject() {
        return Faction.get(fromFaction);
    }

    @Expose
    private final UUID fromCountry;
    public UUID getFromCountry() {
        return fromCountry;
    }
    public Country getFromCountryObject() {
        return Country.get(fromCountry);
    }

    @Expose
    private final UUID toCountry;
    public UUID getToCountry() {
        return toCountry;
    }
    public Country getToCountryObject() {
        return Country.get(toCountry);
    }

    public FactionInvite(UUID uuid, UUID fromFaction, UUID fromCountry, UUID toCountry) {
        this.uuid = uuid;
        this.fromFaction = fromFaction;
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        FactionInvite other = (FactionInvite) otherObject;

        if (uuid == null || other.uuid == null)
            return false;

        return uuid.equals(other.uuid);
    }

    @Override
    public String toString() {
        return "FactionInvite(fromCountry=%s, toCountry=%s)"
               .formatted(fromFaction != null ? getFromFactionObject().getName() : "null",
                          toCountry != null ? getToCountryObject().getName() : "null");
    }
}
