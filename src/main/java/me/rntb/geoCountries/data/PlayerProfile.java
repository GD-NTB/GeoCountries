package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class PlayerProfile extends DataCollection {

    private static final String FILE_PATH = "data/players";
    private static final String DISPLAY_NAME = "PlayerProfile"; // for errors and logging

    // list of every player to have ever joined the server
    public static ArrayList<PlayerProfile> all = null;
    public static List<String> allAsUUIDStrings() {
        return byUUID.keySet().stream().map(UUID::toString).toList();
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

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<PlayerProfile>>() {}.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(FILE_PATH));
            return;
        }

        // populate hashmaps
        for (PlayerProfile player : all) {
            byUsername.put(player.username, player);
            byUUID.put(player.uuid, player);
        }

        if (ConfigState.DebugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " PlayerProfile" + StringUtil.LeadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, all);

        if (ConfigState.DebugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " PlayerProfile" + StringUtil.LeadingS(count) + ".");
        }
    }

    public static void addNew(PlayerProfile player) {
        addNew(player, all, DISPLAY_NAME);

        byUsername.put(player.username, player);
        byUUID.put(player.uuid, player);
    }

    public static void delete(PlayerProfile player) {
        // remove all mentions of this player profile from all countries
        for (Country c : me.rntb.geoCountries.data.Country.all) {
            if (c.leader != null && c.leader.equals(player.uuid)) { c.leader = null; }
            c.citizens.remove(player.uuid);
        }

        byUsername.remove(player.username, player);
        byUUID.remove(player.uuid, player);

        delete(player, all, DISPLAY_NAME);
    }

    // ---

    public UUID uuid;

    public String username; // last known username

    public UUID citizenship = null;
    public Country getCitizenship() {
        return me.rntb.geoCountries.data.Country.byUUID.get(this.citizenship);
    }
    public void setCitizenship(UUID country, PlayerRank rank) {
        this.citizenship = country; this.rank = rank;
    }
    public void setCitizenship(Country country, PlayerRank rank) {
        this.setCitizenship(country.uuid, rank);
    }
    public void clearCitizenship() {
        this.setCitizenship((UUID) null, PlayerRank.NONE);
    }
    public boolean hasCitizenship() {
        return this.citizenship != null;
    }

    public Player getOnlinePlayer() { //
        return Bukkit.getPlayer(this.uuid);
    }
    public OfflinePlayer getOfflinePlayer() { //
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    public enum PlayerRank {
        NONE,
        CITIZEN,
        LEADER
    }
    public PlayerRank rank = PlayerRank.NONE;
    public String getRankString() {
        return switch (this.rank) {
            case NONE -> "None";
            case CITIZEN -> "Citizen";
            case LEADER -> "Leader";
        };
    }
    public int getRankLevel() {
        return this.rank.ordinal();
    }

    public UUID getLeaderOf() {
        return rank == PlayerRank.LEADER ? this.citizenship : null;
    }

//    public List<CitizenshipApplication> getSentCitizenshipApplications() {
//        return CitizenshipApplication.SentAll.stream().filter(ca -> ca.Applicant == this.Uuid).toList();
//    }
//    public List<CitizenshipApplication> getReceivedCitizenshipApplications() {
//        return CitizenshipApplication.SentAll.stream().filter(ca -> ca.Applicant == this.Uuid).toList();
//    }

    public PlayerProfile(Player player) {
        this.username = player.getName();
        this.uuid = player.getUniqueId();
    }

    @Override
    public String toString() {
        return "PlayerProfile(%s, %s)"
                .formatted(this.username, this.uuid.toString());
    }
}
