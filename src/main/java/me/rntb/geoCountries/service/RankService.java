package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;

public class RankService {

    public static void promoteToLeader(PlayerProfile player) {
        Country country = player.getCitizenship();
        if (country == null)
            return;

        if (country.leader != null && country.leader.equals(player.uuid))
            return;

        // ensure player is citizen
        if (!country.citizens.contains(player.uuid) ) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote player to leader to a country they're not a citizen of!");
            return;
        }

        // demote old leader
        PlayerProfile oldLeader = country.getLeader();
        if (oldLeader != null)
            oldLeader.rank = PlayerProfile.PlayerRank.CITIZEN;

        country.leader = player.uuid;
        player.rank = PlayerProfile.PlayerRank.LEADER;
    }

    public static void demoteFromLeader(PlayerProfile player) {
        Country country = player.getCitizenship();
        if (country == null)
            return;

        if (country.leader == null) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote leader but country has no leader!");
            return;
        }

        if (!player.uuid.equals(country.leader)) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote player from leader in a country when they weren't the leader!");
            return;
        }

        country.leader = null;
        player.rank = PlayerProfile.PlayerRank.CITIZEN;
    }
}
