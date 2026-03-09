package me.rntb.geoCountries.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CitizenshipApplication extends DataCollection {

    public static final String FILE_PATH = "data/citizenshipapplications";
    public static final String DISPLAY_NAME = "CitizenshipApplication";

    // list of sent applications
    public static ArrayList<CitizenshipApplication> sentAll = null;
    public static final Map<UUID, CitizenshipApplication> sentByUUID = new HashMap<>();
    public static final Map<UUID, ArrayList<CitizenshipApplication>> sentByApplicant = new HashMap<>();
    public static final Map<UUID, ArrayList<CitizenshipApplication>> sentByToCountry = new HashMap<>();

    // list of all applications currently being written
    public static final ArrayList<CitizenshipApplication> openAll = new ArrayList<>();
    public static final Map<UUID, CitizenshipApplication> openByUUID = new HashMap<>();
    public static final Map<UUID, CitizenshipApplication> openByApplicant = new HashMap<>();

    public static void init() {
        sentAll = readFromFile(CitizenshipApplication.FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<CitizenshipApplication>>() { }.getType());
        if (sentAll == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(FILE_PATH));
            return;
        }
        openAll.clear();

        // reset and populate hashmaps
        sentByUUID.clear();
        sentByApplicant.clear();
        sentByToCountry.clear();
        openByUUID.clear();
        openByApplicant.clear();
        for (CitizenshipApplication cApplication : sentAll) {
            // add to sentByUUID
            sentByUUID.put(cApplication.uuid, cApplication);
            // add to sentByApplicant
            sentByApplicant.computeIfAbsent(cApplication.applicant, v -> new ArrayList<>()).add(cApplication);
            // add to sentByToCountry
            sentByToCountry.computeIfAbsent(cApplication.toCountry, v -> new ArrayList<>()).add(cApplication);
        }

        if (ConfigState.debugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " CitizenApplication" + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, sentAll);

        if (sentAll != null && ConfigState.debugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " CitizenApplication" + StringUtil.leadingS(count) + ".");
        }
    }

    // returns number of citizenship applications purged
    public static int purge() {
        int count = 0;
        for (CitizenshipApplication ca : new ArrayList<>(sentAll)) {
            CitizenshipApplicationService.deleteSent(ca);
            count++;
        }
        return count;
    }

    // ---

    @Expose
    private final UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    @Expose
    private final UUID applicant;
    public UUID getApplicant() {
        return applicant;
    }
    public PlayerProfile getApplicantPlayerProfile() {
        return PlayerProfile.get(applicant);
    }

    @Expose
    private final UUID toCountry;
    public UUID getToCountry() {
        return toCountry;
    }
    public Country getToCountryCountry() {
        return Country.get(toCountry);
    }

    @Expose
    private String reason;
    public String getReason() {
        return reason;
    }
    public void setReason(String value) {
        reason = value;
    }

    // todo: use this value
    @Expose
    private long timeCreated = 0;
    public long getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(long value) {
        timeCreated = value;
    }

    public CitizenshipApplication(UUID uuid, UUID applicant, UUID toCountry) {
        this.uuid = uuid;
        this.applicant = applicant;
        this.toCountry = toCountry;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true;
        if (otherObject == null || getClass() != otherObject.getClass())
            return false;

        CitizenshipApplication other = (CitizenshipApplication) otherObject;

        if (uuid == null || other.uuid == null)
            return false;

        return uuid.equals(other.uuid);
    }

    @Override
    public String toString() {
        PlayerProfile player = PlayerProfile.get(applicant);
        return "CitizenApplication(%s, %s)"
               .formatted(player != null ? player.getUsername() : "null", String.valueOf(uuid));
    }
}
