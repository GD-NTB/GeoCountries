package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
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
    }

    public static void leaveCountry(PlayerProfile playerProfile) {
        Country currentCountry = playerProfile.getCitizenshipCountry();
        if (currentCountry == null)
            return;

        if (playerProfile.getUUID().equals(currentCountry.getLeader()))
            demoteFromLeader(playerProfile);

        currentCountry.getCitizens().remove(playerProfile.getUUID());

        playerProfile.setCitizenshipInternal(null);
        playerProfile.setPositionInternal(Position.NONE);
    }

    public static void promoteToLeader(PlayerProfile playerProfile) {
        Country country = playerProfile.getCitizenshipCountry();
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
        PlayerProfile oldLeader = country.getLeaderPlayerProfile();
        if (oldLeader != null)
            oldLeader.setPositionInternal(PlayerProfile.Position.CITIZEN);

        country.setLeaderInternal(playerProfile.getUUID());
        playerProfile.setPositionInternal(PlayerProfile.Position.LEADER);
    }

    // todo: replace with inheritor when i finally do that shit
    public static void demoteFromLeader(PlayerProfile playerProfile) {
        Country country = playerProfile.getCitizenshipCountry();
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

        country.setLeaderInternal(null);
        playerProfile.setPositionInternal(PlayerProfile.Position.CITIZEN);
    }
}
