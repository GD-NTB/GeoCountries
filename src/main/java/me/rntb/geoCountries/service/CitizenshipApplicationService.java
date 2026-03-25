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
import java.util.UUID;

public class CitizenshipApplicationService {

    // create a new OPEN application
    public static void open(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        CitizenshipApplication.openAll.add(cApplication);
        CitizenshipApplication.openByUUID.put(cApplication.getUUID(), cApplication);
        CitizenshipApplication.openByApplicant.put(cApplication.getApplicant(), cApplication);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Created new open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicantPlayerProfile().getOnlinePlayer(), "§aCreating new citizenship application...");
    }

    // cancel an OPEN application
    public static void cancel(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        CitizenshipApplication.openAll.remove(cApplication);
        CitizenshipApplication.openByUUID.remove(cApplication.getUUID());
        CitizenshipApplication.openByApplicant.remove(cApplication.getApplicant());

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Cancelled open CitizenshipApplication");

        if (sendMessage)
            ChatUtil.sendPrefixedMessage(cApplication.getApplicantPlayerProfile().getOnlinePlayer(), "§aCancelled the citizenship application.");
    }
    public static void onResponseFail(UUID uuid) {
        cancel(CitizenshipApplication.openByApplicant.get(uuid), true);
    }

    // send an OPEN application
    public static void send(CitizenshipApplication cApplication, boolean sendMessage) {
        if (cApplication == null)
            return;

        cancel(cApplication, false); // remove open application

        // if country deleted between opening of application and calling this method, escape
        if (cApplication.getToCountryCountry() == null) {
            if (sendMessage)
                ChatUtil.sendPrefixedMessage(cApplication.getApplicantPlayerProfile().getOnlinePlayer(), "§cCancelled the citizenship application - the recipient country was deleted!");
            return;
        }

        CitizenshipApplication.add(cApplication, CitizenshipApplication.sentAll, CitizenshipApplication.DISPLAY_NAME);

        // add to sentByUUID
        CitizenshipApplication.sentByUUID.put(cApplication.getUUID(), cApplication);
        // add to sentByApplicant
        CitizenshipApplication.sentByApplicant.computeIfAbsent(cApplication.getApplicant(), v -> new ArrayList<>()).add(cApplication);
        // add to sentByToCountry
        CitizenshipApplication.sentByToCountry.computeIfAbsent(cApplication.getToCountry(), v -> new ArrayList<>()).add(cApplication);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent open CitizenshipApplication");

        if (sendMessage) {
            PlayerProfile applicant = cApplication.getApplicantPlayerProfile();
            ChatUtil.sendPrefixedMessage(applicant.getOnlinePlayer(), "§aSent citizenship application to country §f" + cApplication.getToCountryCountry().getName() + "§a!");

            // send notif to leader
            PlayerProfile leaderProfile = cApplication.getToCountryCountry().getLeaderObject();
            if (leaderProfile == null)
                return;
            Player leader = leaderProfile.getOnlinePlayer();

            // build message
            TextComponent.Builder message = Component.text();

            message.append(ChatUtil.mm.deserialize("<gold>Your country has received a citizenship application from <white>" + applicant.getUsername() + "<gold>!"))
                    .append(Component.newline())
                    // [Accept] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicant.getUsername() + "'>" +
                                                    "<hover:show_text:'<white>Click to accept " + applicant.getUsername() + "\\'s application.</white>'>" +
                                                    "<green><bold>[Accept]</bold></green>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [View] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship received " + applicant.getUsername() + "'>" +
                                                    "<hover:show_text:'<white>Click to view " + applicant.getUsername() + "\\'s application.</white>'>" +
                                                    "<white><bold>[View]</bold></white>" +
                                                    "</hover></click>"
                    ))
                    .append(Component.text("  "))
                    // [Reject] button
                    .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicant.getUsername() + "'>" +
                                                    "<hover:show_text:'<white>Click to reject " + applicant.getUsername() + "\\'s application.</white>'>" +
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
        CitizenshipApplication.sentByUUID.remove(cApplication.getUUID());
        // remove from sentByApplicant
        CitizenshipApplication.sentByApplicant.computeIfPresent(cApplication.getApplicant(),
                (k, v) -> { v.remove(cApplication); return v.isEmpty() ? null : v; });
        // remove from sentByToCountry
        CitizenshipApplication.sentByToCountry.computeIfPresent(cApplication.getToCountry(),
                (k, v) -> { v.remove(cApplication); return v.isEmpty() ? null : v; });

        CitizenshipApplication.delete(cApplication, CitizenshipApplication.sentAll, CitizenshipApplication.DISPLAY_NAME);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted sent CitizenshipApplication");
    }

    // accept a SENT application
    public static void accept(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        if (cApplication == null)
            return;

        PlayerProfile player = cApplication.getApplicantPlayerProfile();

        deleteSent(cApplication);

        // don't do anything if already in country
        if (player.getPosition() != PlayerProfile.Position.NONE)
            return;

        CountryService.joinCountry(player, Country.get(cApplication.getToCountry()));

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Accepted sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.get(cApplication.getToCountry());
            ChatUtil.sendPrefixedNotificationMessage(player.getOnlinePlayer(), """
                                                                               §6Your citizenship application was §aaccepted§6.
                                                                               You are now a citizen of §f""" + country.getName() + "§6!");
        }
    }

    // reject a SENT application
    public static void reject(CitizenshipApplication cApplication, boolean sendMessageToApplicant) {
        if (cApplication == null)
            return;

        PlayerProfile player = cApplication.getApplicantPlayerProfile();

        deleteSent(cApplication);

        // don't do anything if already in country
        if (player.getPosition() != PlayerProfile.Position.NONE)
            return;

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Rejected sent CitizenshipApplication");

        if (sendMessageToApplicant) {
            Country country = Country.get(cApplication.getToCountry());
            ChatUtil.sendPrefixedNotificationMessage(player.getOnlinePlayer(), "§6Your citizenship application to §f" + country.getName() + "§6 was §crejected§6.");
        }
    }

    // delete all SENT applications by applicant
    public static void deleteAllSentByApplicant(PlayerProfile playerProfile) {
        if (playerProfile == null)
            return;

        List<CitizenshipApplication> cApplicationsSent = CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID());
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

        List<CitizenshipApplication> cApplicationsReceived = CitizenshipApplication.sentByToCountry.get(country.getUUID());
        if (cApplicationsReceived == null)
            return;
        for (CitizenshipApplication cApplication : new ArrayList<>(cApplicationsReceived)) {
            CitizenshipApplicationService.deleteSent(cApplication);
        }
    }
}
