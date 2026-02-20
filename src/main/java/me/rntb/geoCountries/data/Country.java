package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.types.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

public class Country extends DataCollection {

    private static final String FILE_PATH = "data/countries";
    private static final String DISPLAY_NAME = "Country";

    // list of all countries existing
    public static ArrayList<Country> all = null;
    public static List<String> allAsNames(boolean alphabetical) {
        Stream<String> countries = byName.keySet().stream();
        if (!alphabetical)
            return countries.toList();
        return countries.sorted().toList();
    }

    public static Map<UUID, Country> byUUID = new HashMap<>();
    public static Map<String, Country> byName = new HashMap<>();

    public static Country byCommandSender(CommandSender sender) {
        // get playerprofile of player
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
        if (playerProfile == null)
            return null;
        // get country of sender, returns null on none
        return playerProfile.getCitizenship();
    }

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<Country>>() {}.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, something is very wrong!");
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
        writeToFile(FILE_PATH, DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Saved " + all.size() + " Countries");
    }

    public static void addNew(Country country) {
        addNew(country, all, DISPLAY_NAME);
        byName.put(country.name, country);
        byUUID.put(country.uuid, country);

        country.timeCreated = System.currentTimeMillis();

        // create settings
        country.settings = buildDefaultSettings();
    }

    public static void delete(Country country) {
        // clear all citizen's citizenships
        for (UUID uuid : new ArrayList<>(country.citizens)) { // new arraylist while we're modifying
            PlayerProfile player = PlayerProfile.byUUID.get(uuid);
            if (player != null)
                player.clearCitizenship();
        }

        byName.remove(country.name);
        byUUID.remove(country.uuid);

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
    public PlayerProfile getLeader() {
        return PlayerProfile.byUUID.get(leader);
    }
    public void setLeader(PlayerProfile player) {
        // if clearing leader, set to null and escape
        if (player == null) {
            this.leader = null;
            return;
        }

        // if has leader
        if (this.leader != null) {
            // if player is already leader, escape
            if (this.leader.equals(player.uuid))
                return;

            // demote old
            PlayerProfile old = PlayerProfile.byUUID.get(this.leader);
            old.setRank(PlayerProfile.PlayerRank.CITIZEN);
        }

        // set player to leader and add as citizen if not already
        this.leader = player.uuid;
        addCitizen(player);
        player.rank = PlayerProfile.PlayerRank.LEADER; // re-set rank
    }

    public ArrayList<UUID> citizens = new ArrayList<>();
    public List<PlayerProfile> citizensSortedByRank() {
        return this.citizens.stream()
                            .map(uuid -> PlayerProfile.byUUID.get(uuid))
                            .sorted(Comparator.comparing(PlayerProfile::getRankLevel))
                            .toList().reversed();
    }
    public int citizenCount() {
        return citizens.size();
    }
    public void addCitizen(PlayerProfile player) {
        if (!this.citizens.contains(player.uuid)) {
            this.citizens.add(player.uuid);
            player.citizenship = this.uuid;
        }
    }
    public void removeCitizen(PlayerProfile player) {
        this.citizens.remove(player.uuid);
        if (player.citizenship != null && player.citizenship.equals(this.uuid))
            player.citizenship = null;
    }

    // settings
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
        Instant instant = Instant.ofEpochMilli(this.timeCreated);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }

    public List<String> getReceivedCitizenshipApplicationsAsStrings() {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(this.uuid);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getApplicant().username).toList();
    }

    public Country() { }

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
