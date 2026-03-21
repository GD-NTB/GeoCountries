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

    // if was leader, sets new leader to random member, so make sure to demote first!
    public static void leaveFaction(Country country) {
        Faction currentFaction = country.getFactionFaction();
        if (currentFaction == null)
            return;

        if (country.getUUID().equals(currentFaction.getLeader()))
            FactionService.demoteFromLeader(country, null);

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
        if (!faction.getMembers().contains(country.getUUID())) {
            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogErrorMessage("Tried to promote country to leader of a faction they're not a citizen of!");
            return;
        }

        faction.setLeaderInternal(country.getUUID());
    }

    // if newLeader = null, a random other member is chosen to be the new leader
    public static void demoteFromLeader(Country country, Country newLeader) {
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

        // if new leader null, set random other member country to new leader
        if (newLeader == null) {
            Country newRandomLeader = Country.get(faction.getMembers().stream()
                                                                      .filter(c -> !c.equals(country.getUUID()))
                                                                      .findFirst().orElse(null));
            // if no others, disband
            if (newRandomLeader == null)
                disband(faction);
            else
                promoteToLeader(newRandomLeader);
        }
        else
            promoteToLeader(newLeader);
    }

    public static void disband(Faction faction) {
        ChatUtil.broadcastPrefixedMessage("§6The faction §f" + faction.getName() + "§6 has just been disbanded!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToFaction(faction, "§6Your faction has just been disbanded! §cYou are no longer a member of any faction.", true);

        faction.deregister();
    }
}
