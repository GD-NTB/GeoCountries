package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;

import java.util.*;
import java.util.stream.Stream;

public class CountryData extends DataCollection {

    private static final String FILE_PATH = "CountryData";
    private static final String DISPLAY_NAME = "CountryData"; // for errors and logging

    // list of countrydatas of every country existing
    public static ArrayList<CountryData> All = null;
    public static List<String> AllAsNames(boolean sortedAlphabetically) {
        Stream<String> countries = CountryDataByName.keySet().stream();
        if (!sortedAlphabetically) { return countries.toList(); }
        return countries.sorted().toList();
    }

    public static Map<String, CountryData> CountryDataByName = new HashMap<>();
    public static Map<UUID, CountryData> CountryDataByUUID = new HashMap<>();
    public static CountryData GetCountryDataByUUIDString(String uuid) {
        try {
            return CountryDataByUUID.getOrDefault(UUID.fromString(uuid), null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void Init() {
        All = ReadFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<CountryData>>() {}.getType());

        // create hashmaps
        for (CountryData cd : All) {
            CountryDataByName.put(cd.Name, cd);
            CountryDataByUUID.put(cd.Uuid, cd);
        }
    }

    public static void Save() {
        WriteToFile(FILE_PATH, DISPLAY_NAME, All);
    }

    public static void AddNew(CountryData CountryData) {
        AddNew(CountryData, All, DISPLAY_NAME);

        CountryDataByName.put(CountryData.Name, CountryData);
        CountryDataByUUID.put(CountryData.Uuid, CountryData);
    }

    public static void Delete(CountryData countryData) {
        // remove all mentions of this countrydata from all playerdatas
        for (PlayerData pd : PlayerData.All) {
            if (pd.hasCountry() && pd.Country.equals(countryData.Uuid)) { pd.clearCountry(); }
        }

        CountryDataByName.remove(countryData.Name, countryData);
        CountryDataByUUID.remove(countryData.Uuid, countryData);

        Delete(countryData, All, DISPLAY_NAME);
    }

    // ---

    public String Name;
    public String getName() { return this.Name; }
    public void setName(String name) {
        CountryDataByName.put(name, CountryDataByName.remove(this.Name));
        this.Name = name;
    }

    public UUID Uuid;

    public UUID Leader = null;
    public PlayerData getLeader() { return PlayerData.PlayerDataByUUID.getOrDefault(Leader, null); }

    public ArrayList<UUID> Citizens = new ArrayList<>();
    public List<PlayerData> CitizensSortedByRank() {
        return this.Citizens.stream().map(uuid -> PlayerData.PlayerDataByUUID.get(uuid)).sorted(Comparator.comparing(PlayerData::getRankLevel)).toList();
    }
    public int CitizenCount() { return Citizens.size(); }

    public CountryData(String name, UUID uuid) {
        this.Name = name;
        this.Uuid = uuid;
    }

    @Override
    public String toString() {
        return String.format("CountryData(%s, Leader=%s, Citizens=%d)",
                             this.Name, this.getLeader().Username, this.CitizenCount());
    }

    public enum NameValidation {
        WRONGSIZE,
        SPECIALCHARS,
        ALREADYEXISTS,
        OK
    }
    public static String GetNameValidationString(NameValidation validation) {
        return switch (validation) {
            case NameValidation.WRONGSIZE -> "§cCountry name must be between §f3-35 characters§c characters!§r";
            case NameValidation.SPECIALCHARS -> "§cCountry name can only contain letters, numbers, and apostrophes!§r";
            case NameValidation.ALREADYEXISTS -> "§cA country with that name already exists!§r";
//            case NameValidation.OK -> "§aCountry name was valid!§r";
            default -> "§cCountry name was invalid!§r";
        };
    }
    public static NameValidation ValidateName(String name, boolean alreadyExistsInvalid) {
        // between 3-35 chars
        if (name.length() < 3 || name.length() >= 35) {
            return NameValidation.WRONGSIZE;
        }
        // no special characters
        if (name.matches(".*[^a-zA-Z0-9'-].*\n")) {
            return NameValidation.SPECIALCHARS;
        }
        // country already exists
        if (alreadyExistsInvalid && CountryData.CountryDataByName.getOrDefault(name, null) != null) {
            return NameValidation.ALREADYEXISTS;
        }

        return NameValidation.OK;
    }
}
