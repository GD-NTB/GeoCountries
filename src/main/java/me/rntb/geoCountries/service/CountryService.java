package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.util.ChatUtil;

public class CountryService {

    public static void joinCountry(PlayerProfile playerProfile, Country country) {
        leaveCountry(playerProfile);

        // add to new country
        country.getCitizens().add(playerProfile.getUUID());

        playerProfile.setCitizenshipInternal(country.getUUID());
        playerProfile.setPositionInternal(Position.CITIZEN);

        // remove all pending citizenship applications
        CitizenshipApplicationService.deleteAllSentByApplicant(playerProfile);

        IntegrationManager.onStyleUpdate(country);
    }

    // if was leader, sets new leader to random citizen, so make sure to demote first!
    public static void leaveCountry(PlayerProfile playerProfile) {
        Country currentCountry = playerProfile.getCitizenshipObject();
        if (currentCountry == null)
            return;

        if (playerProfile.getUUID().equals(currentCountry.getLeader()))
            demoteFromLeader(playerProfile, null);

        currentCountry.getCitizens().remove(playerProfile.getUUID());

        playerProfile.setCitizenshipInternal(null);
        playerProfile.setPositionInternal(Position.NONE);

        IntegrationManager.onStyleUpdate(currentCountry);
    }

    public static void promoteToLeader(PlayerProfile playerProfile) {
        Country country = playerProfile.getCitizenshipObject();
        if (country == null)
            return;

        // if already leader, dont do anything
        if (country.getLeader() != null && country.getLeader().equals(playerProfile.getUUID()))
            return;

        // ensure player is citizen
        if (!country.getCitizens().contains(playerProfile.getUUID()) ) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote player to leader to a country they're not a citizen of!");
            return;
        }

        // demote old leader
        PlayerProfile oldLeader = country.getLeaderObject();
        if (oldLeader != null)
            oldLeader.setPositionInternal(PlayerProfile.Position.CITIZEN);

        country.setLeaderInternal(playerProfile.getUUID());
        playerProfile.setPositionInternal(PlayerProfile.Position.LEADER);

        IntegrationManager.onStyleUpdate(country);
    }

    // this should pretty much never be called without a specific reason
    // if newLeader = null, a random other citizen is chosen to be the new leader
    // does not update maps
    public static void demoteFromLeader(PlayerProfile playerProfile, PlayerProfile newLeader) {
        Country country = playerProfile.getCitizenshipObject();
        if (country == null)
            return;

        if (country.getLeader() == null) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote leader but country has no leader!");
            return;
        }

        if (!playerProfile.getUUID().equals(country.getLeader())) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote player from leader in a country when they weren't the leader!");
            return;
        }

        playerProfile.setPositionInternal(PlayerProfile.Position.CITIZEN);

        // if new leader null, set random other member country to new leader
        if (newLeader == null) {
            PlayerProfile newRandomLeader = PlayerProfile.get(country.getCitizens().stream()
                                                                                   .filter(p -> !p.equals(playerProfile.getUUID()))
                                                                                   .findFirst().orElse(null));
            // if no others, disband
            if (newRandomLeader == null)
                dissolve(country);
            else
                promoteToLeader(newRandomLeader);
        }
        else
            promoteToLeader(newLeader);
    }

    public static void dissolve(Country country) {
        ChatUtil.broadcastPrefixedMessage("§6The country §f" + country.getName() + "§6 has just been dissolved!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6Your country has just been dissolved! §cYou are no longer a citizen of any country.", true);

        country.deregister();
    }
}
