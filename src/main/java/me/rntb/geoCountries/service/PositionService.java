package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;

public class PositionService {

    public static void promoteToLeader(PlayerProfile playerProfile) {
        Country country = playerProfile.getCitizenshipCountry();
        if (country == null)
            return;

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
