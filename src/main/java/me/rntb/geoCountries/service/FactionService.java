package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.util.ChatUtil;

public class FactionService {

    public static void joinFaction(Faction faction, Country country) {
        if (faction == null || country == null)
            return;

        leaveFaction(faction, country);

        // add to new country
        faction.getMembers().add(country.getUUID());

        country.setFactionInternal(faction.getUUID());

        // remove all pending faction invites
        FactionInviteService.deleteAllSentToCountry(country);

        IntegrationManager.onStyleUpdate(country);
    }

    // if was leader, sets new leader to random member, so make sure to demote first!
    public static void leaveFaction(Faction faction, Country country) {
        if (faction == null || country == null || !country.hasFaction() || !country.getFactionObject().equals(faction))
            return;

        if (country.getUUID().equals(faction.getLeader()))
            FactionService.demoteFromLeader(faction, country, null);

        faction.getMembers().remove(country.getUUID());
        country.setFactionInternal(null);

        IntegrationManager.onStyleUpdate(country);
    }

    public static void promoteToLeader(Faction faction, Country country) {
        if (faction == null || country == null || !country.hasFaction() || !country.getFactionObject().equals(faction))
            return;

        // if already leader, dont do anything
        if (faction.getLeader() != null && faction.getLeader().equals(country.getUUID()))
            return;

        // ensure country is member
        if (!faction.getMembers().contains(country.getUUID())) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote country to leader of a faction they're not a citizen of!");
            return;
        }

        faction.setLeaderInternal(country.getUUID());

        IntegrationManager.onStyleUpdate(country);
    }

    // if newLeader = null, a random other member is chosen to be the new leader
    public static void demoteFromLeader(Faction faction, Country country, Country newLeader) {
        if (faction == null || country == null || !country.hasFaction() || !country.getFactionObject().equals(faction))
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

        // if new leader null, set random other member country to new leader
        if (newLeader == null) {
            Country newRandomLeader = Country.get(faction.getMembers().stream()
                                                                      .filter(c -> !c.equals(country.getUUID()))
                                                                      .findFirst().orElse(null));
            // if no others, disband
            if (newRandomLeader == null)
                disband(faction);
            else
                promoteToLeader(faction, newRandomLeader);
        }
        else
            promoteToLeader(faction, newLeader);
    }

    public static void disband(Faction faction) {
        if (faction == null)
            return;

        ChatUtil.broadcastPrefixedMessage("§6The faction §3" + faction.getName() + "§6 has just been disbanded!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToFaction(faction, "§6Your faction has just been disbanded! §cYou are no longer a member of any faction.", true);

        faction.deregister();
    }
}
