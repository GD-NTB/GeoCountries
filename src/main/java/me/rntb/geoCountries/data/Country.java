package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

public class Country extends DataCollection {

    public static String filePath;
    public static String displayName;

    // list of all countries existing
    public static ArrayList<Country> all = null;
    public static List<String> allAsStrings(boolean alphabetical) {
        Stream<String> countries = byName.keySet().stream();
        if (!alphabetical)
            return countries.toList();
        return countries.sorted().toList();
    }

    private static final Map<UUID, Country> byUUID = new HashMap<>();
    public static Country get(UUID uuid) {
        return byUUID.get(uuid);
    }
    private static final Map<String, Country> byName = new HashMap<>();
    public static Country get(String name) {
        return byName.get(name);
    }

    public static void init() {
        filePath = "data/countries";
        displayName = "Country";

        all = readFromFile(filePath, displayName, new TypeToken<ArrayList<Country>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(filePath));
            return;
        }

        // reset and populate hashmaps, load settings
        byUUID.clear();
        byName.clear();
        for (Country country : all) {
            byUUID.put(country.uuid, country);
            byName.put(country.name, country);

            // load settings by defaultSettings
            LinkedHashMap<String, String> orderedSettings = new LinkedHashMap<>();
            settingsData.forEach((key, settingData) -> orderedSettings.put(key, country.settings.getOrDefault(key, settingData.defaultValue)));
            country.settings = orderedSettings;
        }

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Loaded " + all.size() + " Countries");
    }

    public static void save() {
        writeToFile(Country.filePath, Country.displayName, all);

        if (all != null && ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Saved " + all.size() + " Countries");
    }

    // returns number of countries purged
    public static int purge() {
        int count = 0;
        for (Country c : new ArrayList<>(all)) {
            c.deregister();
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, displayName);
        byName.put(name, this);
        byUUID.put(uuid, this);

        this.timeCreated = System.currentTimeMillis();

        // create settings
        this.settings = buildDefaultSettings();
    }

    public void deregister() {
        // clear all citizen's citizenships
        for (UUID uuid : new ArrayList<>(citizens)) { // new arraylist while we're modifying
            PlayerProfile player = PlayerProfile.get(uuid);
            if (player != null)
                CitizenshipService.leaveCountry(player);
        }

        byName.remove(name);
        byUUID.remove(uuid);

        // delete any associated applications
        CitizenshipApplicationService.deleteAllSentByToCountry(this);

        delete(this, all, displayName);
    }

    // ---

    public UUID uuid;

    public String name;
    public void setName(String name) {
        byName.put(name, this);
        this.name = name;
    }

    public UUID leader = null;
    public PlayerProfile getLeader() {
        return PlayerProfile.get(leader);
    }

    public ArrayList<UUID> citizens = new ArrayList<>();
    public List<Player> getOnlineCitizens() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : citizens) {
            PlayerProfile profile = PlayerProfile.get(uuid);
            if (profile == null)
                continue;

            Player player = profile.getOnlinePlayer();
            if (player == null)
                continue;

            players.add(player);
        }

        return players;
    }
    public List<String> citizensAsStrings() {
        return citizens.stream()
                       .map(uuid -> PlayerProfile.get(uuid).username)
                       .toList();
    }
    public List<PlayerProfile> citizensSortedByPosition() {
        return citizens.stream()
                       .map(PlayerProfile::get)
                       .sorted(Comparator.comparing(PlayerProfile::getPositionLevel))
                       .toList().reversed();
    }
    public int citizenCount() {
        return citizens.size();
    }

    public LinkedHashMap<String, String> settings = new LinkedHashMap<>();
    public static final LinkedHashMap<String, SettingData> settingsData = new LinkedHashMap<>() {{
        put("motto", new SettingData("null",
                                     SettingData.Type.COUNTRY_MOTTO,
                                     "Motto",
                                     "The motto of the country"));
        put("prefixenabled", new SettingData("true",
                                             SettingData.Type.BOOL,
                                             "Prefix Enabled",
                                             "Whether or not to show the country prefix in chat messages"));
        put("prefix", new SettingData("null",
                                      SettingData.Type.COUNTRY_PREFIX,
                                      "Prefix",
                                      "The prefix to show in its citizens' chat messages, doesn't show if set to null",
                                      ConfigState.countryPrefixMin, ConfigState.countryPrefixMax));
        put("prefixcolour", new SettingData("DARK_GREY",
                                            SettingData.Type.CHAT_COLOUR,
                                            "Prefix Colour",
                                            "The colour of the prefix to show in its citizens' chat messages"));
        put("autoacceptcitizenshipapplications", new SettingData("false",
                                                                 SettingData.Type.BOOL,
                                                                 "Auto-Accept Citizenship Applications",
                                                                 "Automatically accept citizenship applications when received"));

    }};
    public static LinkedHashMap<String, String> buildDefaultSettings() {
        return settingsData.entrySet().stream()
                                     .collect(LinkedHashMap::new,
                                              (m, e) -> m.put(e.getKey(), e.getValue().defaultValue),
                                              LinkedHashMap::putAll);
    }

    public long timeCreated = 0;
    public String timeCreatedAsString() {
        Instant instant = Instant.ofEpochMilli(timeCreated);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }

    public List<String> getReceivedCitizenshipApplicationsAsUsernames() {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(uuid);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getApplicant().username).toList();
    }

    public Country(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country(name=%s, leader=%s, citizens=%d)"
                .formatted(this.name, this.getLeader().username, this.citizenCount());
    }
}
