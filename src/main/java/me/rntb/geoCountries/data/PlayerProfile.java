package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

public class PlayerProfile extends DataCollection {

    private static final String FILE_PATH = "data/players";
    private static final String DISPLAY_NAME = "PlayerProfile"; // for errors and logging

    // list of every player to have ever joined the server
    public static ArrayList<PlayerProfile> all = null;
    public static List<String> allAsUUIDStrings() {
        return byUUID.keySet().stream()
                              .map(UUID::toString).toList();
    }
    public static List<String> allAsUsernames(boolean alphabetical) {
        Stream<String> usernames = byUsername.keySet().stream();
        if (!alphabetical) return usernames.toList();
        return usernames.sorted().toList();
    }

    public static Map<UUID, PlayerProfile> byUUID = new HashMap<>();
    public static Map<String, PlayerProfile> byUsername = new HashMap<>();

    public static PlayerProfile byUUIDString(String uuid) {
        try {
            return byUUID.get(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static PlayerProfile byCommandSender(CommandSender sender) {
        if (!(sender instanceof Player player))
            return null;
        return get(player);
    }

    public static PlayerProfile get(Player player) {
        return byUUID.get(player.getUniqueId());
    }

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<PlayerProfile>>() {}.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(FILE_PATH));
            return;
        }

        // reset and populate hashmaps, load settings
        byUUID.clear();
        byUsername.clear();
        for (PlayerProfile player : all) {
            byUsername.put(player.username, player);
            byUUID.put(player.uuid, player);

            // load settings by defaultSettings
            LinkedHashMap<String, String> orderedSettings = new LinkedHashMap<>();
            settingsData.forEach((key, settingData) -> orderedSettings.put(key, player.settings.getOrDefault(key, settingData.defaultValue)));
            player.settings = orderedSettings;
        }

        if (ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " PlayerProfile" + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " PlayerProfile" + StringUtil.leadingS(count) + ".");
        }
    }

    public static void addNew(PlayerProfile player) {
        addNew(player, all, DISPLAY_NAME);

        byUsername.put(player.username, player);
        byUUID.put(player.uuid, player);

        player.timeFirstJoined = System.currentTimeMillis();

        // create settings
        player.settings = buildDefaultSettings();
    }

    public static void delete(PlayerProfile player) {
        // remove all mentions of this player profile from all countries
        for (Country c : me.rntb.geoCountries.data.Country.all) {
            if (c.leader != null && c.leader.equals(player.uuid)) { c.leader = null; }
            c.citizens.remove(player.uuid);
        }

        byUsername.remove(player.username);
        byUUID.remove(player.uuid);

        delete(player, all, DISPLAY_NAME);
    }

    // ---

    public UUID uuid;

    public String username; // last known username

    public UUID citizenship = null;
    public Country getCitizenship() {
        return me.rntb.geoCountries.data.Country.byUUID.get(citizenship);
    }
    public void setCitizenship(UUID country, PlayerRank rank) {
        UUID prevCountryUUID = citizenship;

        // if removing player country
        if (prevCountryUUID != null && !prevCountryUUID.equals(country)) {
            Country prevCountry = Country.byUUID.get(prevCountryUUID);

            if (prevCountry != null) {
                prevCountry.removeCitizen(this);

                if (uuid.equals(prevCountry.leader))
                    prevCountry.setLeader(null);
            }
        }

        // set citizenship
        citizenship = country;

        // update rank
        setRank(rank);
    }
    public void setCitizenship(Country country, PlayerRank rank) {
        setCitizenship(country.uuid, rank);
    }
    public void clearCitizenship() {
        setCitizenship((UUID) null, PlayerRank.NONE);
    }
    public boolean hasCitizenship() {
        return citizenship != null;
    }

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public enum PlayerRank {
        NONE,
        CITIZEN,
        LEADER
    }
    public PlayerRank rank = PlayerRank.NONE;
    public String getRankString() {
        return switch (rank) {
            case NONE -> "None";
            case CITIZEN -> "Citizen";
            case LEADER -> "Leader";
        };
    }
    public int getRankLevel() {
        return rank.ordinal();
    }
    public void setRank(PlayerRank newRank) {
        // if no change, escape
        if (rank == newRank)
            return;

        Country country = Country.byUUID.get(citizenship);
        // if not part of country, dont do anything except set to NONE
        if (country == null) {
            rank = PlayerRank.NONE;
            return;
        }

        // if demoting completely, remove rank and escape
        if (newRank == PlayerRank.NONE) {
            // remove from citizens list if needed
            if (uuid.equals(country.leader)) {
                country.setLeader(null);
            }

            country.removeCitizen(this);

            citizenship = null;
            rank = PlayerRank.NONE;
            return;
        }

        // upon gaining any kind of citizenship, cancel all previous citizenship applications
        if (CitizenshipApplication.sentByApplicant.get(uuid) != null)
            CitizenshipApplication.deleteAllSentByApplicant(this);

        // set rank in country
        if (newRank == PlayerRank.LEADER) {
            country.setLeader(this);
            rank = PlayerRank.LEADER;
            return;
        }

        if (newRank == PlayerRank.CITIZEN) {
            // if was leader, remove leadership
            if (uuid.equals(country.leader)) {
                country.setLeader(null); // or country.removeLeader()
            }

            country.addCitizen(this);
            rank = PlayerRank.CITIZEN;
            return;
        }

        // finally set rank property if didnt get set
        rank = newRank;
    }

    // settings
    public LinkedHashMap<String, String> settings = new LinkedHashMap<>();
    public static final LinkedHashMap<String, SettingData> settingsData = new LinkedHashMap<>() {{
        put("soundeffects", new SettingData("true",
                                            SettingData.Type.BOOL,
                                            "Sound Effects",
                                            "Play sound effects"));
        put("chatnotificationsounds", new SettingData("true",
                                                      SettingData.Type.BOOL,
                                                      "Chat Notification Sounds",
                                                      "Play a ding sound effect when receiving important chat messages"));
    }};
    public static LinkedHashMap<String, String> buildDefaultSettings() {
        return settingsData.entrySet().stream()
                                     .collect(LinkedHashMap::new,
                                             (m, e) -> m.put(e.getKey(), e.getValue().defaultValue),
                                             LinkedHashMap::putAll);
    }

    public long timeFirstJoined = 0;
    public String timeFirstJoinedAsString() {
        Instant instant = Instant.ofEpochMilli(this.timeFirstJoined);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(StringUtil.timeFormatter);
    }

    public UUID getLeaderOf() {
        return rank == PlayerRank.LEADER ? this.citizenship : null;
    }

    public List<String> getSentCitizenshipApplicationsAsStrings() {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(this.uuid);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getToCountry().name).toList();
    }

    public PlayerProfile() { }

    public PlayerProfile(Player player) {
        this.username = player.getName();
        this.uuid = player.getUniqueId();
    }

    @Override
    public String toString() {
        return "PlayerProfile(%s, %s)"
               .formatted(this.username, String.valueOf(this.uuid));
    }
}
