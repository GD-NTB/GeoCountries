package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.*;

public class CitizenshipApplication extends DataCollection {

    private static final String FILE_PATH = "data/citizenshipapplications";
    private static final String DISPLAY_NAME = "CitizenshipApplication";

    // list of sent applications
    public static ArrayList<CitizenshipApplication> sentAll = null;
    public static Map<UUID, CitizenshipApplication> sentByUUID = new HashMap<>();
    public static Map<UUID, ArrayList<CitizenshipApplication>> sentByApplicant = new HashMap<>();
    public static Map<UUID, ArrayList<CitizenshipApplication>> sentByToCountry = new HashMap<>();

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
        openByUUID.remove(cApplication.uuid);
        openByApplicant.remove(cApplication.applicant);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Cancelled open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aCancelled the citizenship application.");
    }
    // send an open application
    public static void send(CitizenshipApplication cApplication, boolean sendMessage) {
        cancel(cApplication, false); // remove open application

        addNew(cApplication, sentAll, DISPLAY_NAME);

        // add to sentByUUID
        sentByUUID.put(cApplication.uuid, cApplication);
        // add to sentByApplicant
        sentByApplicant.computeIfAbsent(cApplication.applicant, v -> new ArrayList<>()).add(cApplication);
        // add to sentByToCountry
        sentByToCountry.computeIfAbsent(cApplication.toCountry, v -> new ArrayList<>()).add(cApplication);

        cApplication.timeCreated = System.currentTimeMillis();

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent open CitizenshipApplication");

        if (sendMessage) {
            PlayerProfile applicant = cApplication.getApplicant();
            ChatUtil.sendPrefixedMessage(applicant.getOnlinePlayer(), "§aSent citizenship application to country §f" + cApplication.getToCountry().name + "§a!");

            // send notif to leader
            PlayerProfile leaderProfile = cApplication.getToCountry().getLeader();
            if (leaderProfile == null)
                return;
            Player leader = leaderProfile.getOnlinePlayer();

            // build message
            TextComponent.Builder message = Component.text();
            MiniMessage mm = MiniMessage.miniMessage();

            message.append(mm.deserialize("<gold>Your country has received a citizenship application from <white>" + applicant.username + "<gold>!"))
                   .append(Component.newline())
                    // [Accept] button
                    .append(mm.deserialize("<click:run_command:'/gc citizenship accept " + applicant.username + "'>" +
                                           "<hover:show_text:'<dark_gray>Click to accept <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                           "<green><bold>[Accept]</bold></green>" +
                                           "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [View] button
                    .append(mm.deserialize("<click:run_command:'/gc citizenship received " + applicant.username + "'>" +
                                           "<hover:show_text:'<dark_gray>Click to view <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                           "<white><bold>[View]</bold></white>" +
                                           "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [Reject] button
                    .append(mm.deserialize("<click:run_command:'/gc citizenship reject " + applicant.username + "'>" +
                                           "<hover:show_text:'<dark_gray>Click to reject <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                           "<red><bold>[Reject]</bold></red>" +
                                           "</hover></click>"));
            // send message to leader
            ChatUtil.sendPrefixedMessage(leader, message.build());
            // play sound to leader
            SoundUtil.playSound(leader, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    // delete a sent application
    public static void deleteSent(CitizenshipApplication cApplication) {
        // remove from sentByUUID
        sentByUUID.remove(cApplication.uuid);
        // remove from sentByApplicant
        List<CitizenshipApplication> cApplicationsSent = sentByApplicant.get(cApplication.applicant);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(cApplication);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                sentByApplicant.remove(cApplication.applicant);
        }
        // remove from sentByToCountry
        cApplicationsSent = sentByToCountry.get(cApplication.toCountry);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(cApplication);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                sentByApplicant.remove(cApplication.toCountry);
        }

        delete(cApplication, sentAll, DISPLAY_NAME);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted sent CitizenshipApplication");
    }
    public static void deleteAllSentByApplicant(PlayerProfile player) {
        List<CitizenshipApplication> cApplicationsSent = sentByApplicant.get(player.uuid);
        if (cApplicationsSent == null)
            return;
        for (CitizenshipApplication cApplication : new ArrayList<>(cApplicationsSent)) {
            CitizenshipApplication.deleteSent(cApplication);
        }
    }
    // accept a sent application
    public static void accept(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        // give citizenship to applicant
        PlayerProfile playerProfile = cApplication.getApplicant();
        // just in case
        if (playerProfile.rank != PlayerProfile.PlayerRank.NONE) {
            deleteSent(cApplication);
            return;
        }
        playerProfile.setCitizenship(cApplication.toCountry, PlayerProfile.PlayerRank.CITIZEN);

        // delete sent application
        deleteSent(cApplication);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Accepted sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.byUUID.get(cApplication.toCountry);
            Player player = playerProfile.getOnlinePlayer();
            ChatUtil.sendPrefixedMessage(player, """
                                                 §6Your citizenship application was §aaccepted§6.
                                                 You are now a citizen of §f""" + country.name + "§6!");
            // play sound effect
            SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    // reject a sent application
    public static void reject(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        // get applicant
        PlayerProfile playerProfile = cApplication.getApplicant();
        // just in case
        if (playerProfile.rank != PlayerProfile.PlayerRank.NONE) {
            deleteSent(cApplication);
            return;
        }

        // delete sent application
        deleteSent(cApplication);

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Rejected sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.byUUID.get(cApplication.toCountry);
            Player player = playerProfile.getOnlinePlayer();
            ChatUtil.sendPrefixedMessage(player, "§6Your citizenship application to §f" + country.name + "§6 was §crejected§6.");
            // play sound effect
            SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }

    public static void init() {
        sentAll = readFromFile(FILE_PATH, DISPLAY_NAME, new TypeToken<ArrayList<CitizenshipApplication>>() {}.getType());
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

        if (ConfigState.DebugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Loaded " + count + " CitizenApplication" + StringUtil.leadingS(count) + ".");
        }
    }

    public static void save() {
        writeToFile(FILE_PATH, DISPLAY_NAME, sentAll);

        if (sentAll != null && ConfigState.DebugLogging) {
            int count = sentAll.size();
            ChatUtil.sendPrefixedLogMessage("Saved " + count + " CitizenApplication" + StringUtil.leadingS(count) + ".");
        }
    }

    // ---

    public UUID uuid;

    public UUID applicant;
    public PlayerProfile getApplicant() { return PlayerProfile.byUUID.get(this.applicant); }

    public UUID toCountry;
    public Country getToCountry() { return Country.byUUID.get(this.toCountry); }

    public String reason;

    public long timeCreated = 0; // set in sent method

    public CitizenshipApplication(UUID uuid, UUID applicant, UUID toCountry) {
        this.uuid = uuid;
        this.applicant = applicant;
        this.toCountry = toCountry;
    }

    @Override
    public String toString() {
        PlayerProfile applicant = PlayerProfile.byUUID.get(this.applicant);
        return "CitizenApplication(%s, %s)"
                .formatted(applicant != null ? applicant.username : null, String.valueOf(this.uuid));
    }
}
