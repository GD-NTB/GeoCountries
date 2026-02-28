package me.rntb.geoCountries.data;

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

    public static String filePath;
    public static String displayName;

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
        filePath = "data/citizenshipapplications";
        displayName = "CitizenshipApplication";

        sentAll = readFromFile(CitizenshipApplication.filePath, displayName, new TypeToken<ArrayList<CitizenshipApplication>>() {}.getType());
        if (sentAll == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(filePath));
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
        writeToFile(filePath, displayName, sentAll);

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

    public UUID uuid;

    public UUID applicant;
    public PlayerProfile getApplicant() { return PlayerProfile.get(applicant); }

    public UUID toCountry;
    public Country getToCountry() { return Country.get(this.toCountry); }

    public String reason;

    public long timeCreated = 0;

    public CitizenshipApplication(UUID uuid, UUID applicant, UUID toCountry) {
        this.uuid = uuid;
        this.applicant = applicant;
        this.toCountry = toCountry;
    }

    @Override
    public String toString() {
        PlayerProfile player = PlayerProfile.get(applicant);
        return "CitizenApplication(%s, %s)"
                .formatted(player != null ? player.username : "null", String.valueOf(uuid));
    }
}
