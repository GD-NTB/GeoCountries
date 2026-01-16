package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CitizenshipApplication extends DataCollection {

    private static final String FILE_PATH = "data/citizenshipapplications";
    private static final String DISPLAY_NAME = "CitizenshipApplication";

    // list of sent applications
    public static ArrayList<CitizenshipApplication> sentAll = null;
    public static Map<UUID, CitizenshipApplication> sentByUUID = new HashMap<>();
    public static Map<UUID, CitizenshipApplication> sentByApplicant = new HashMap<>();

    // list of all applications currently being written
    public static ArrayList<CitizenshipApplication> openAll = new ArrayList<>();
    public static Map<UUID, CitizenshipApplication> openByUUID = new HashMap<>();
    public static Map<UUID, CitizenshipApplication> openByApplicant = new HashMap<>();

    // create a new open application
    public static void open(CitizenshipApplication cApplication, boolean sendMessage) {
        openAll.add(cApplication);
        openByUUID.put(cApplication.uuid, cApplication);
        openByApplicant.put(cApplication.applicant, cApplication);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Created new open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aCreating new citizenship application...");
    }
    // cancel an open application
    public static void cancel(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        openAll.remove(cApplication);
        openByUUID.remove(cApplication.uuid, cApplication);
        openByApplicant.remove(cApplication.applicant, cApplication);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Cancelled open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aCancelled the citizenship application.");
    }
    // send an open application
    public static void send(CitizenshipApplication cApplication, boolean sendMessage) {
        cancel(cApplication, false); // remove open application

        addNew(cApplication, sentAll, DISPLAY_NAME);
        sentByApplicant.put(cApplication.applicant, cApplication);
        sentByUUID.put(cApplication.uuid, cApplication);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aSent citizenship application to country §f%s§a!"
                                                                                      .formatted(cApplication.getCountry().name));
    }
    public static void deleteSent(CitizenshipApplication cApplication) {
        sentByApplicant.remove(cApplication.applicant, cApplication);
        sentByUUID.remove(cApplication.uuid, cApplication);

        delete(cApplication, sentAll, DISPLAY_NAME);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted sent CitizenshipApplication");
    }
    // todo: Accept()/Reject()

    public static void init() {
        sentAll = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<CitizenshipApplication>>() {}.getType());
        if (sentAll == null) {
            ChatUtil.sendPrefixedLogMessage("ReadFromFile(%s) was null, try deleting the file!"
                                            .formatted(FILE_PATH));
            return;
        }

        // populate hashmaps
        for (CitizenshipApplication cApplication : sentAll) {
            sentByApplicant.put(cApplication.applicant, cApplication);
            sentByUUID.put(cApplication.uuid, cApplication);
        }

        if (ConfigState.DebugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " CitizenApplication" + StringUtil.LeadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, sentAll);

        if (ConfigState.DebugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " CitizenApplication" + StringUtil.LeadingS(count) + ".");
        }
    }

    // ---

    public UUID uuid;

    public UUID applicant;
    public PlayerProfile getApplicant() { return PlayerProfile.byUUID.get(this.applicant); }

    public UUID toCountry;
    public Country getCountry() { return Country.byUUID.get(this.toCountry); }

    public String reason;

    public CitizenshipApplication(UUID uuid, UUID applicant, UUID toCountry) {
        this.uuid = uuid;
        this.applicant = applicant;
        this.toCountry = toCountry;
    }

    @Override
    public String toString() {
        PlayerProfile applicant = PlayerProfile.byUUID.get(this.applicant);
        return "CitizenApplication(%s, %s)"
                .formatted(applicant != null ? applicant.username : null,
                           this.uuid.toString());
    }
}
