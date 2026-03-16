package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.util.ChatUtil;

public class FactionService {

    public static void joinFaction(Country country, Faction faction) {
        leaveFaction(country);

        // add to new country
        faction.getMembers().add(country.getUUID());

        country.setFactionInternal(faction.getUUID());

        // remove all pending faction invites
        FactionInviteService.deleteAllSentToCountry(country);
    }

    public static void leaveFaction(Country country) {
        Faction currentFaction = country.getFactionFaction();
        if (currentFaction == null)
            return;

        if (country.getUUID().equals(currentFaction.getLeader()))
            FactionService.demoteFromLeader(country);

        currentFaction.getMembers().remove(country.getUUID());

        country.setFactionInternal(null);
    }

    public static void promoteToLeader(Country country) {
        Faction faction = country.getFactionFaction();
        if (faction == null)
            return;

        // if already leader, dont do anything
        if (faction.getLeader() != null && faction.getLeader().equals(country.getUUID()))
            return;

        // ensure country is member
        if (!faction.getMembers().contains(country.getUUID()) ) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote country to leader of a faction they're not a citizen of!");
            return;
        }

        faction.setLeaderInternal(country.getUUID());
    }

    // todo: replace with inheritor when i finally do that shit
    public static void demoteFromLeader(Country country) {
        Faction faction = country.getFactionFaction();
        if (faction == null)
            return;

        if (faction.getLeader() == null) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote leader but faction has no leader!");
            return;
        }

        if (!country.getUUID().equals(faction.getLeader())) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to demote country from leader in a faction when they weren't the leader!");
            return;
        }

        country.setLeaderInternal(null);
    }
}
