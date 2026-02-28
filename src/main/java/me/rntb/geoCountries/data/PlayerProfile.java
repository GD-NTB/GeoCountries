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
        filePath = "data/players";
        displayName = "PlayerProfile";

        all = readFromFile(filePath, displayName, new TypeToken<ArrayList<PlayerProfile>>() {}.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(filePath));
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
        writeToFile(filePath, displayName, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " PlayerProfile" + StringUtil.leadingS(count) + ".");
        }
    }

    public void register() {
        add(this, all, displayName);

        byUsername.put(this.username, this);
        byUUID.put(this.uuid, this);

        timeFirstJoined = System.currentTimeMillis();

        settings = buildDefaultSettings();
    }

    public void deregister() {
        // remove all mentions of this player profile from all countries
        for (Country c : me.rntb.geoCountries.data.Country.all) {
            if (c.leader != null && c.leader.equals(uuid)) { c.leader = null; }
            c.citizens.remove(uuid);
        }

        byUsername.remove(username);
        byUUID.remove(uuid);

        delete(this, all, displayName);
    }

    // ---

    public UUID uuid;

    public String username; // last known username

    public UUID citizenship = null;
    public Country getCitizenship() {
        return Country.byUUID.get(citizenship);
    }
    public boolean hasCitizenship() {
        return citizenship != null;
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

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
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
