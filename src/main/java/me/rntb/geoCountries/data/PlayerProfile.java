package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class PlayerProfile extends DataCollection {

    public static final String FILE_PATH = "data/players";
    public static final String DISPLAY_NAME = "PlayerProfile";

    // list of every player to have ever joined the server
    public static ArrayList<PlayerProfile> all = null;
    public static List<String> allAsUUIDStrings() {
        return byUUID.keySet().stream()
                              .map(UUID::toString).toList();
    }
    public static List<String> allAsUsernames(boolean alphabetical) {
        Stream<String> usernames = byUsername.keySet().stream();
        if (alphabetical) return usernames.sorted().toList();
        return usernames.toList();
    }

    private static final Map<UUID, PlayerProfile> byUUID = new HashMap<>();
    public static PlayerProfile get(UUID uuid) {
        return byUUID.get(uuid);
    }
    private static final Map<String, PlayerProfile> byUsername = new HashMap<>();
    public static PlayerProfile get(String username) {
        return byUsername.get(username);
    }

    public static PlayerProfile byUUIDString(String uuid) {
        try {
            return byUUID.get(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static PlayerProfile get(Player player) {
        return byUUID.get(player.getUniqueId());
    }
    public static PlayerProfile get(CommandSender sender) {
        if (!(sender instanceof Player player))
            return null;
        return get(player);
    }

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<PlayerProfile>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
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
            if (player.settings == null)
                player.settings = new LinkedHashMap<>();
            LinkedHashMap<String, String> orderedSettings = new LinkedHashMap<>();
            settingsData.forEach((key, settingData) -> orderedSettings.put(key, player.settings.getOrDefault(key, settingData.defaultValue)));
            player.settings = orderedSettings;
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

    // returns number of playerprofiles purged
    public static int purge() {
        int count = 0;
        for (PlayerProfile p : new ArrayList<>(all)) {
            p.deregister();
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, DISPLAY_NAME);

        byUsername.put(username, this);
        byUUID.put(uuid, this);

        settings = buildDefaultSettings();
    }

    public void deregister() {
        // remove all mentions of this player profile from all countries
        for (Country c : me.rntb.geoCountries.data.Country.all) {
            if (c.getLeader() != null && c.getLeader().equals(uuid))
                c.setLeaderInternal(null);
            c.getCitizens().remove(uuid);
        }

        byUsername.remove(username);
        byUUID.remove(uuid);

        delete(this, all, DISPLAY_NAME);
    }

    // ---

    @Expose
    private final UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    @Expose
    private String username; // last known username
    public String getUsername() {
        return username;
    }
    public void setUsername(String value) {
        username = value;
        byUsername.put(value, this);
    }

    @Expose
    private UUID citizenship = null;
    public UUID getCitizenship() {
        return citizenship;
    }
    public void setCitizenshipInternal(UUID value) {
        citizenship = value;
    }
    public Country getCitizenshipCountry() {
        return Country.get(citizenship);
    }
    public boolean hasCitizenship() {
        return citizenship != null;
    }

    public enum Position {
        NONE,
        CITIZEN,
        LEADER
    }
    @Expose
    private Position position = Position.NONE;
    public Position getPosition() {
        return position;
    }
    public void setPositionInternal(Position value) {
        position = value;
    }
    public String getPositionString() {
        return switch (position) {
            case NONE -> "None";
            case CITIZEN -> "Citizen";
            case LEADER -> "Leader";
        };
    }
    public int getPositionLevel() {
        return position.ordinal();
    }

    @Expose
    private LinkedHashMap<String, String> settings = new LinkedHashMap<>();
    public LinkedHashMap<String, String> getSettings() {
        return settings;
    }
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

    public List<String> getSentCitizenshipApplicationsAsNames() {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(uuid);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getToCountryCountry().getName()).toList();
    }

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public PlayerProfile(Player player) {
        this.username = player.getName();
        this.uuid = player.getUniqueId();
    }

    public Chunk getBukkitChunk() {
        Player player = getOnlinePlayer();
        if (player == null)
            return null;
        return player.getChunk();
    }
    public ClaimChunk getClaimChunk() {
        Player player = getOnlinePlayer();
        if (player == null)
            return null;
        return ClaimChunk.get(player.getChunk());
    }
    public String getChunkString() {
        Chunk chunk = getOnlinePlayer().getChunk();
        return "§8(%d, %d)§r"
               .formatted(chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        PlayerProfile other = (PlayerProfile) otherObject;

        if (this.uuid == null || other.uuid == null)
            return false;

        return this.uuid.equals(other.uuid);
    }

    @Override
    public String toString() {
        return "PlayerProfile(%s, %s)"
               .formatted(username, String.valueOf(uuid));
    }
}
