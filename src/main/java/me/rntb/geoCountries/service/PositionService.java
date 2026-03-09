package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;

public class PositionService {

    public static void promoteToLeader(PlayerProfile player) {
        Country country = player.getCitizenshipCountry();
        if (country == null)
            return;

        if (country.getLeader() != null && country.getLeader().equals(player.getUUID()))
            return;

        // ensure player is citizen
        if (!country.getCitizens().contains(player.getUUID()) ) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote player to leader to a country they're not a citizen of!");
            return;
        }

        // demote old leader
        PlayerProfile oldLeader = country.getLeaderPlayerProfile();
        if (oldLeader != null)
            oldLeader.setPositionInternal(PlayerProfile.Position.CITIZEN);

        country.setLeaderInternal(player.getUUID());
        player.setPositionInternal(PlayerProfile.Position.LEADER);
    }

    public static void demoteFromLeader(PlayerProfile player) {
        Country country = player.getCitizenshipCountry();
        if (country == null)
            return;

        if (country.getLeader() == null) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote leader but country has no leader!");
            return;
        }

        if (!player.getUUID().equals(country.getLeader())) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote player from leader in a country when they weren't the leader!");
            return;
        }

        country.setLeaderInternal(null);
        player.setPositionInternal(PlayerProfile.Position.CITIZEN);
    }
}
