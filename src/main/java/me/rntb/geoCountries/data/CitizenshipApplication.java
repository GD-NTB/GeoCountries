package me.rntb.geoCountries.data;

import com.google.gson.reflect.TypeToken;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

    public void open(boolean sendMessage) {
        openAll.add(this);
        openByUUID.put(this.uuid, this);
        openByApplicant.put(this.applicant, this);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Created new open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(this.getApplicant().getOnlinePlayer(), "§aCreating new citizenship application...");
    }
    public void cancel(boolean sendMessage) {
        openAll.remove(this);
        openByUUID.remove(this.uuid);
        openByApplicant.remove(this.applicant);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Cancelled open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(this.getApplicant().getOnlinePlayer(), "§aCancelled the citizenship application.");
    }
    public void send(boolean sendMessage) {
        cancel(false); // remove open application

        addNew(this, sentAll, DISPLAY_NAME);

        // add to sentByUUID
        sentByUUID.put(this.uuid, this);
        // add to sentByApplicant
        sentByApplicant.computeIfAbsent(this.applicant, v -> new ArrayList<>()).add(this);
        // add to sentByToCountry
        sentByToCountry.computeIfAbsent(this.toCountry, v -> new ArrayList<>()).add(this);

        this.timeCreated = System.currentTimeMillis();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent open CitizenshipApplication");

        if (sendMessage) {
            PlayerProfile applicant = this.getApplicant();
            ChatUtil.sendPrefixedMessage(applicant.getOnlinePlayer(), "§aSent citizenship application to country §f" + this.getToCountry().name + "§a!");

            // send notif to leader
            PlayerProfile leaderProfile = this.getToCountry().getLeader();
            if (leaderProfile == null)
                return;
            Player leader = leaderProfile.getOnlinePlayer();

            // build message
            TextComponent.Builder message = Component.text();

            message.append(ChatUtil.mm.deserialize("<gold>Your country has received a citizenship application from <white>" + applicant.username + "<gold>!"))
                   .append(Component.newline())
                    // [Accept] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicant.username + "'>" +
                                                    "<hover:show_text:'<dark_gray>Click to accept <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                                    "<green><bold>[Accept]</bold></green>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [View] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship received " + applicant.username + "'>" +
                                                    "<hover:show_text:'<dark_gray>Click to view <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                                    "<white><bold>[View]</bold></white>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [Reject] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicant.username + "'>" +
                                                    "<hover:show_text:'<dark_gray>Click to reject <white>" + applicant.username + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                                    "<red><bold>[Reject]</bold></red>" +
                                                    "</hover></click>"));
            // send message to leader
            ChatUtil.sendPrefixedMessage(leader, message.build());
            // play sound to leader
            SoundUtil.playSound(leader, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    public void deleteSent() {
        // remove from sentByUUID
        sentByUUID.remove(this.uuid);
        // remove from sentByApplicant
        List<CitizenshipApplication> cApplicationsSent = sentByApplicant.get(this.applicant);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(this);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                sentByApplicant.remove(this.applicant);
        }
        // remove from sentByToCountry
        cApplicationsSent = sentByToCountry.get(this.toCountry);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(this);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                sentByApplicant.remove(this.toCountry);
        }

        delete(this, sentAll, DISPLAY_NAME);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted sent CitizenshipApplication");
    }
    public void accept(boolean sendMessageToApplicant) {
        PlayerProfile playerProfile = this.getApplicant();

        deleteSent();
        // just in case
        if (playerProfile.rank != PlayerProfile.PlayerRank.NONE) {
            deleteSent();
            return;
        }

        playerProfile.setCitizenship(this.toCountry, PlayerProfile.PlayerRank.CITIZEN);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Accepted sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.byUUID.get(this.toCountry);
            Player player = playerProfile.getOnlinePlayer();
            ChatUtil.sendPrefixedMessage(player, """
                                                 §6Your citizenship application was §aaccepted§6.
                                                 You are now a citizen of §f""" + country.name + "§6!");
            // play sound effect
            SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    public void reject(boolean sendMessageToApplicant) {
        PlayerProfile playerProfile = this.getApplicant();

        deleteSent();

        // just in case
        if (playerProfile.rank != PlayerProfile.PlayerRank.NONE) {
            return;
        }

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Rejected sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.byUUID.get(this.toCountry);
            Player player = playerProfile.getOnlinePlayer();
            ChatUtil.sendPrefixedMessage(player, "§6Your citizenship application to §f" + country.name + "§6 was §crejected§6.");
            // play sound effect
            SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    public static void deleteAllSentByApplicant(PlayerProfile player) {
        List<CitizenshipApplication> cApplicationsSent = sentByApplicant.get(player.uuid);
        if (cApplicationsSent == null)
            return;
        for (CitizenshipApplication cApplication : new ArrayList<>(cApplicationsSent)) {
            cApplication.deleteSent();
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

    // ---

    public UUID uuid;

    public UUID applicant;
    public PlayerProfile getApplicant() { return PlayerProfile.byUUID.get(this.applicant); }

    public UUID toCountry;
    public Country getToCountry() { return Country.byUUID.get(this.toCountry); }

    public String reason;

    public long timeCreated = 0;

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
