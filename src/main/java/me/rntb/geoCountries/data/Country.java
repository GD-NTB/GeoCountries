package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;

import java.util.*;
import java.util.stream.Stream;

public class Country extends DataCollection {

    private static final String FILE_PATH = "data/countries";
    private static final String DISPLAY_NAME = "Country";

    // list of all countries existing
    public static ArrayList<Country> all = null;
    public static List<String> allAsNames(boolean alphabetical) {
        Stream<String> countries = byName.keySet().stream();
        if (!alphabetical) return countries.toList();
        return countries.sorted().toList();
    }

    public static Map<UUID, Country> byUUID = new HashMap<>();
    public static Map<String, Country> byName = new HashMap<>();

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<Country>>() {}.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, something is very wrong!");
            return;
        }

        // populate hashmaps
        for (Country country : all) {
            byName.put(country.name, country);
            byUUID.put(country.uuid, country);
        }

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Loaded " + all.size() + " Countries");
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, all);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Saved " + all.size() + " Countries");
    }

    public static void addNew(Country country) {
        addNew(country, all, DISPLAY_NAME);
        byName.put(country.name, country);
        byUUID.put(country.uuid, country);
    }

    public static void delete(Country country) {
        // remove all mentions of this country from all playerprofiles
        for (PlayerProfile p : PlayerProfile.all) {
            if (p.hasCitizenship() && p.citizenship.equals(country.uuid))
                p.clearCitizenship();
        }

        byName.remove(country.name, country);
        byUUID.remove(country.uuid, country);

        delete(country, all, DISPLAY_NAME);
    }

    // ---

    public UUID uuid;

    public String name;
    public void setName(String name) {
        byName.put(name, byName.remove(this.name));
        this.name = name;
    }

    public UUID leader = null;
    public PlayerProfile getLeader() { return PlayerProfile.byUUID.get(leader); }

    public ArrayList<UUID> citizens = new ArrayList<>();
    public List<PlayerProfile> citizensSortedByRank() {
        return this.citizens.stream().map(uuid -> PlayerProfile.byUUID.get(uuid)).sorted(Comparator.comparing(PlayerProfile::getRankLevel)).toList();
    }
    public int citizenCount() { return citizens.size(); }
    
    public Country(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country(name=%s, leader=%s, citizens=%d)"
                .formatted(this.name,
                           this.getLeader().username,
                           this.citizenCount());
    }

}
