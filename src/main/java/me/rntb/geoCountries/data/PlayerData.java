package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerData extends DataCollection {

    private static final String FILE_PATH = "PlayerData";
    private static final String DISPLAY_NAME = "PlayerData"; // for errors and logging

    // list of playerdatas of every person to have ever joined the server
    public static ArrayList<PlayerData> All = null;
    public static List<String> AllAsUsernames() { return PlayerDataByUsername.keySet().stream().toList(); }
    public static List<UUID> AllAsUUID() { return PlayerDataByUUID.keySet().stream().toList(); }
    public static List<String> AllAsUUIDStrings() { return PlayerDataByUUID.keySet().stream().map(UUID::toString).toList(); }

    public static Map<String, PlayerData> PlayerDataByUsername = new HashMap<>();
    public static Map<UUID, PlayerData> PlayerDataByUUID = new HashMap<>();
    public static PlayerData GetPlayerDataByUUIDString(String uuid) {
        try {
            return PlayerDataByUUID.getOrDefault(UUID.fromString(uuid), null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void Init() {
        All = ReadFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<PlayerData>>() {}.getType());

        // create hashmaps
        for (PlayerData pd : All) {
            PlayerDataByUsername.put(pd.Username, pd);
            PlayerDataByUUID.put(pd.Uuid, pd);
        }
    }

    public static void Save() {
        WriteToFile(FILE_PATH, DISPLAY_NAME, All);
    }

    public static void AddNew(PlayerData playerData) {
        AddNew(playerData, All, DISPLAY_NAME);

        PlayerDataByUsername.put(playerData.Username, playerData);
        PlayerDataByUUID.put(playerData.Uuid, playerData);
    }

    public static void Delete(PlayerData playerData) {
        // remove all mentions of this playerdata from all countrydatas
        for (CountryData cd : CountryData.All) {
            if (cd.Leader != null && cd.Leader.equals(playerData.Uuid)) { cd.Leader = null; }
            cd.Citizens.remove(playerData.Uuid);
        }

        PlayerDataByUsername.remove(playerData.Username, playerData);
        PlayerDataByUUID.remove(playerData.Uuid, playerData);

        Delete(playerData, All, DISPLAY_NAME);
    }

    // ---

    public String Username;
    public UUID Uuid;

    public UUID Country = null;
    public CountryData getCountry() { return CountryData.CountryDataByUUID.getOrDefault(this.Country, null); }
    public void setCountry(UUID country, PlayerRank rank) { this.Country = country; this.Rank = rank; }
    public void setCountry(CountryData countryData, PlayerRank rank) { this.setCountry(countryData.Uuid, rank); }
    public void clearCountry() { this.setCountry((UUID) null, PlayerRank.NONE); }
    public boolean hasCountry() { return this.Country != null; }

    public enum PlayerRank {
        NONE,
        CITIZEN,
        LEADER
    }
    public PlayerRank Rank = PlayerRank.NONE;
    public String getRankString() {
        return switch (this.Rank) {
            case NONE -> "None";
            case CITIZEN -> "Citizen";
            case LEADER -> "Leader";
        };
    }
    public int getRankLevel() { return this.Rank.ordinal(); } // higher = better

    public UUID getLeaderOf() { return Rank == PlayerRank.LEADER ? this.Country : null; }

    public PlayerData(String username, UUID uuid) {
        this.Username = username;
        this.Uuid = uuid;
    }

    public PlayerData(Player player) {
        this(player.getName(), player.getUniqueId());
    }

    @Override
    public String toString() {
        return String.format("PlayerData(%s, %s)",
                             this.Username, this.Uuid.toString());
    }
}
