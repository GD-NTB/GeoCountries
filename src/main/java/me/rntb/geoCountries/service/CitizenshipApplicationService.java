package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CitizenshipApplicationService {

    // create a new OPEN application
    public static void open(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        CitizenshipApplication.openAll.add(cApplication);
        CitizenshipApplication.openByUUID.put(cApplication.uuid, cApplication);
        CitizenshipApplication.openByApplicant.put(cApplication.applicant, cApplication);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Created new open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aCreating new citizenship application...");
    }

    // cancel an OPEN application
    public static void cancel(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        CitizenshipApplication.openAll.remove(cApplication);
        CitizenshipApplication.openByUUID.remove(cApplication.uuid);
        CitizenshipApplication.openByApplicant.remove(cApplication.applicant);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Cancelled open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§aCancelled the citizenship application.");
    }

    // send an OPEN application
    public static void send(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        cancel(cApplication, false); // remove open application

        // if country deleted between opening of application and calling this method, escape
        if (cApplication.getToCountry() == null) {
            if (sendMessage)
                ChatUtil.sendPrefixedMessage(cApplication.getApplicant().getOnlinePlayer(), "§cCancelled the citizenship application - the recipient country was deleted!");
            return;
        }

        CitizenshipApplication.add(cApplication, CitizenshipApplication.sentAll, CitizenshipApplication.displayName);

        // add to sentByUUID
        CitizenshipApplication.sentByUUID.put(cApplication.uuid, cApplication);
        // add to sentByApplicant
        CitizenshipApplication.sentByApplicant.computeIfAbsent(cApplication.applicant, v -> new ArrayList<>()).add(cApplication);
        // add to sentByToCountry
        CitizenshipApplication.sentByToCountry.computeIfAbsent(cApplication.toCountry, v -> new ArrayList<>()).add(cApplication);

        cApplication.timeCreated = System.currentTimeMillis();

        if (ConfigState.debugLogging)
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

            message.append(ChatUtil.mm.deserialize("<gold>Your country has received a citizenship application from <white>" + applicant.username + "<gold>!"))
                    .append(Component.newline())
                    // [Accept] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicant.username + "'>" +
                                                    "<hover:show_text:'<white>Click to accept " + applicant.username + "\\'s application.</white>'>" +
                                                    "<green><bold>[Accept]</bold></green>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [View] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship received " + applicant.username + "'>" +
                                                    "<hover:show_text:'<white>Click to view " + applicant.username + "\\'s application.</white>'>" +
                                                    "<white><bold>[View]</bold></white>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [Reject] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicant.username + "'>" +
                                                    "<hover:show_text:'<white>Click to reject " + applicant.username + "\\'s application.</white>'>" +
                                                    "<red><bold>[Reject]</bold></red>" +
                                                    "</hover></click>"));
            // send message to leader
            ChatUtil.sendPrefixedNotificationMessage(leader, message.build());
        }
    }

    // delete a SENT application
    public static void deleteSent(CitizenshipApplication cApplication) {
        if (cApplication == null)
            return;

        // remove from sentByUUID
        CitizenshipApplication.sentByUUID.remove(cApplication.uuid);
        // remove from sentByApplicant
        List<CitizenshipApplication> cApplicationsSent = CitizenshipApplication.sentByApplicant.get(cApplication.applicant);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(cApplication);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                CitizenshipApplication.sentByApplicant.remove(cApplication.applicant);
        }
        // remove from sentByToCountry
        cApplicationsSent = CitizenshipApplication.sentByToCountry.get(cApplication.toCountry);
        if (cApplicationsSent != null) {
            cApplicationsSent.remove(cApplication);
            // delete entry if list is now empty
            if (cApplicationsSent.isEmpty())
                CitizenshipApplication.sentByApplicant.remove(cApplication.toCountry);
        }

        CitizenshipApplication.delete(cApplication, CitizenshipApplication.sentAll, CitizenshipApplication.displayName);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted sent CitizenshipApplication");
    }

    // accept a SENT application
    public static void accept(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        if (cApplication == null)
            return;

        PlayerProfile player = cApplication.getApplicant();

        deleteSent(cApplication);

        // don't do anything if already in country
        if (player.rank != PlayerProfile.PlayerRank.NONE)
            return;

        CitizenshipService.joinCountry(player, Country.get(cApplication.toCountry));

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Accepted sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.get(cApplication.toCountry);
            ChatUtil.sendPrefixedNotificationMessage(player.getOnlinePlayer(), """
                                                                               §6Your citizenship application was §aaccepted§6.
                                                                               You are now a citizen of §f""" + country.name + "§6!");
        }
    }

    // reject a SENT application
    public static void reject(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        if (cApplication == null)
            return;

        PlayerProfile player = cApplication.getApplicant();

        deleteSent(cApplication);

        // don't do anything if already in country
        if (player.rank != PlayerProfile.PlayerRank.NONE)
            return;

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Rejected sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.get(cApplication.toCountry);
            ChatUtil.sendPrefixedNotificationMessage(player.getOnlinePlayer(), "§6Your citizenship application to §f" + country.name + "§6 was §crejected§6.");
        }
    }

    // delete all SENT applications by applicant
    public static void deleteAllSentByApplicant(PlayerProfile player) {
        if (player == null)
            return;

        List<CitizenshipApplication> cApplicationsSent = CitizenshipApplication.sentByApplicant.get(player.uuid);
        if (cApplicationsSent == null)
            return;
        for (CitizenshipApplication cApplication : new ArrayList<>(cApplicationsSent)) {
            CitizenshipApplicationService.deleteSent(cApplication);
        }
    }

    // delete all SENT applications by country sent to
    public static void deleteAllSentByToCountry(Country country) {
        if (country == null)
            return;

        List<CitizenshipApplication> cApplicationsReceived = CitizenshipApplication.sentByToCountry.get(country.uuid);
        if (cApplicationsReceived == null)
            return;
        for (CitizenshipApplication cApplication : new ArrayList<>(cApplicationsReceived)) {
            CitizenshipApplicationService.deleteSent(cApplication);
        }
    }
}
