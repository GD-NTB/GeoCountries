package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.service.FactionService;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class Country extends DataCollection {

    public static final String FILE_PATH = "data/countries";
    public static final String DISPLAY_NAME = "Country";

    // list of all countries existing
    public static ArrayList<Country> all = null;
    public static List<String> allAsNames(boolean alphabetical) {
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
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<Country>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(FILE_PATH));
            return;
        }

        // reset and populate hashmaps, load settings
        byUUID.clear();
        byName.clear();
        for (Country country : all) {
            byUUID.put(country.uuid, country);
            byName.put(country.name, country);

            // load settings by defaultSettings
            if (country.settings == null)
                country.settings = new LinkedHashMap<>();
            LinkedHashMap<String, String> orderedSettings = new LinkedHashMap<>();
            settingsData.forEach((key, settingData) -> orderedSettings.put(key, country.settings.getOrDefault(key, settingData.defaultValue)));
            country.settings = orderedSettings;
        }

        if (ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(Country.FILE_PATH, Country.DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static int purge() {
        int count = 0;
        for (Country c : new ArrayList<>(all)) {
            c.deregister();
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, DISPLAY_NAME);
        byName.put(name, this);
        byUUID.put(uuid, this);

        // create settings
        settings = buildDefaultSettings();
    }

    public void deregister() {
        // clear from factions
        FactionService.leaveFaction(this);

        // clear all citizen's citizenships
        for (UUID uuid : new ArrayList<>(citizens)) { // new arraylist while we're modifying
            PlayerProfile playerProfile = PlayerProfile.get(uuid);
            if (playerProfile != null) {
                playerProfile.setCitizenshipInternal(null);
                playerProfile.setPositionInternal(Position.NONE);
            }
        }

        // delete claimed chunks
        for (Long claimChunkKey : new ArrayList<>(claimChunks)) {
            ClaimChunk.get(claimChunkKey).deregister();
        }

        // delete any associated applications
        CitizenshipApplicationService.deleteAllSentByToCountry(this);
        // delete all sent faction invites
        FactionInviteService.deleteAllSentToCountry(this);

        byName.remove(name);
        byUUID.remove(uuid);

        delete(this, all, DISPLAY_NAME);
    }

    // ---

    @Expose
    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    @Expose
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String value) {
        byName.remove(name);
        name = value;
        byName.put(name, this);
    }
    public String getNameAndFaction() {
        if (hasFaction())
            return "%s (§3%s§f)"
                   .formatted(getName(),
                              getFactionFaction().getName());
        else
            return getName();
    }

    @Expose(serialize = false, deserialize = false)
    private UUID leader = null;
    public UUID getLeader() {
      return leader;
    }
    public PlayerProfile getLeaderPlayerProfile() {
        return PlayerProfile.get(leader);
    }
    public void setLeaderInternal(UUID value) {
        leader = value;
    }
    public boolean isLeaderOnline() {
        if (leader == null)
            return false;
        return Bukkit.getPlayer(PlayerProfile.get(leader).getUsername()) != null;
    }

    @Expose(serialize = false, deserialize = false)
    private final ArrayList<UUID> citizens = new ArrayList<>();
    public ArrayList<UUID> getCitizens() {
        return citizens;
    }
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
    public List<String> getCitizensAsStrings() {
        return citizens.stream()
                       .map(uuid -> PlayerProfile.get(uuid).getUsername()).toList();
    }
    public List<PlayerProfile> getCitizensSortedByPosition() {
        return citizens.stream()
                       .map(PlayerProfile::get)
                       .sorted(Comparator.comparing(PlayerProfile::getPositionLevel))
                       .toList().reversed();
    }
    public int getCitizenCount() {
        return citizens.size();
    }

    @Expose
    private LinkedHashMap<String, String> settings = new LinkedHashMap<>();
    public LinkedHashMap<String, String> getSettings() {
        return settings;
    }
    public static final LinkedHashMap<String, SettingData> settingsData = new LinkedHashMap<>() {{
        put("mapcolour", new SettingData("#FF0000",
                                         SettingData.Type.COLOUR,
                                         "Map Colour",
                                         "The colour of the country's claims on map plugins."));
        put("motto", new SettingData("null",
                                     SettingData.Type.COUNTRY_MOTTO,
                                     "Motto",
                                     "The motto of the country"));
        // todo: delete this setting
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

    // loaded in ClaimChunk.init
    @Expose(serialize = false, deserialize = false)
    private List<Long> claimChunks;
    public List<Long> getClaimChunks() {
        return claimChunks;
    }
    public int getClaimChunksCount() {
        return claimChunks.size();
    }

    // loaded in Faction.init
    @Expose(serialize = false, deserialize = false)
    private UUID faction;
    public UUID getFaction() {
        return faction;
    }
    public Faction getFactionFaction() {
        return Faction.get(faction);
    }
    public void setFactionInternal(UUID value) {
        faction = value;
    }
    public boolean hasFaction() {
        return faction != null;
    }

    public List<String> getReceivedCitizenshipApplicationsAsUsernames() {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(uuid);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getApplicantPlayerProfile().getUsername()).toList();
    }

    // same faction = dark green
    // else white
    public String getChatColourBetweenOtherCountry(Country otherCountry) {
        // if in same faction, dark green
        Faction factionA = getFactionFaction();
        Faction factionB = otherCountry.getFactionFaction();
        if (factionA != null && (factionA.equals(factionB))) // Faction.equals checks for null
            return "§2";

        return "§f";
    }

    // gson constructor
    public Country() {
        claimChunks = new ArrayList<>();
    }

    public Country(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        Country other = (Country) otherObject;

        if (uuid == null || other.uuid == null)
            return false;

        return uuid.equals(other.uuid);
    }

    @Override
    public String toString() {
        return "Country(name=%s, leader=%s, citizens=%d)"
               .formatted(name, getLeaderPlayerProfile().getUsername(), getCitizenCount());
    }
}
