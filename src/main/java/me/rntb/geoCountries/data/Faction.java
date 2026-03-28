package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;

import java.util.*;
import java.util.stream.Stream;

public class Faction extends DataCollection {

    public static final String FILE_PATH = "data/factions";
    public static final String DISPLAY_NAME = "Faction";

    // list of all factions existing
    public static ArrayList<Faction> all = null;
    public static List<String> getAllAsNames(boolean alphabetical) {
        Stream<String> countries = byName.keySet().stream();
        if (!alphabetical)
            return countries.toList();
        return countries.sorted().toList();
    }

    private static final Map<UUID, Faction> byUUID = new HashMap<>();
    public static Faction get(UUID uuid) {
        return byUUID.get(uuid);
    }

    private static final Map<String, Faction> byName = new HashMap<>();
    public static Faction get(String name) {
        return byName.get(name);
    }

    public static void init() {
        all = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<Faction>>() { }.getType());
        if (all == null) {
            ChatUtil.sendPrefixedLogErrorMessage("ReadFromFile(%s) was null, try backing it up and deleting the file!"
                                                 .formatted(FILE_PATH));
            return;
        }

        // reset and populate hashmaps
        byUUID.clear();
        byName.clear();
        for (Faction faction : all) {
            byUUID.put(faction.uuid, faction);
            byName.put(faction.name, faction);

            // set faction of member countries
            if (faction.members == null) {
                faction.members = new ArrayList<>();
                faction.members.add(faction.leader);
            }
            for (UUID memberUUID : faction.members) {
                Country.get(memberUUID).setFactionInternal(faction.uuid);
            }
        }

        if (ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(Faction.FILE_PATH, Faction.DISPLAY_NAME, all);

        if (all != null && ConfigState.debugLogging) {
            int count = all.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " " + DISPLAY_NAME + StringUtil.leadingS(count) + ".");
        }
    }

    public static int purge() {
        int count = 0;
        for (Faction c : new ArrayList<>(all)) {
            c.deregister();
            count++;
        }
        return count;
    }

    public void register() {
        add(this, all, DISPLAY_NAME);
        byUUID.put(uuid, this);
        byName.put(name, this);

        // set faction of member countries
        for (UUID memberUUID : members) {
            Country.get(memberUUID).setFactionInternal(uuid);
        }
    }

    public void deregister() {
        // remove members and update maps of their country
        for (UUID memberUUID : new ArrayList<>(members)) {
            Country member = Country.get(memberUUID);
            member.setFactionInternal(null);
            IntegrationManager.onStyleUpdate(member);
        }

        // delete all sent faction invites
        FactionInviteService.deleteAllSentByFaction(this);

        byUUID.remove(uuid);
        byName.remove(name);

        delete(this, all, DISPLAY_NAME);
    }

    // ---

    @Expose
    private final UUID uuid;
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

    @Expose
    private UUID leader;
    public UUID getLeader() {
        return leader;
    }
    public Country getLeaderObject() {
        return Country.get(leader);
    }
    public void setLeaderInternal(UUID value) {
        leader = value;
    }

    @Expose
    private List<UUID> members = new ArrayList<>();
    public List<UUID> getMembers() {
        return members;
    }
    public int getMemberCount() {
        return members.size();
    }
    public List<Country> getMembersSorted() {
        return Stream.concat(Stream.of(getLeaderObject()),
                             members.stream().filter(m -> !m.equals(leader))
                                             .map(Country::get))
                     .toList();
    }

    public int getTotalClaimChunks() {
        int count = 0;
        for (UUID memberUUID : members) {
            count += Country.get(memberUUID).getClaimChunksCount();
        }
        return count;
    }

    public Faction(UUID uuid, String name, UUID leader) {
        this.uuid = uuid;
        this.name = name;
        this.leader = leader;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        Faction other = (Faction) otherObject;

        return uuid == other.uuid;
    }

    @Override
    public String toString() {
        return "Faction(leader=%s, members=%d)"
               .formatted(leader, getMemberCount());
    }
}
