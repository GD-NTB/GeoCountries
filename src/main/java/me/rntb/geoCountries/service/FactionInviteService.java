package me.rntb.geoCountries.service;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionInviteService {

    // send a faction invite
    public static void send(FactionInvite fInvite, boolean sendMessage) {
        if (fInvite == null)
            return;

        FactionInvite.add(fInvite, FactionInvite.all, FactionInvite.DISPLAY_NAME);

        // add to byUUID
        FactionInvite.byUUID.put(fInvite.getUUID(), fInvite);
        // add to byFromFaction
        FactionInvite.byFromFaction.computeIfAbsent(fInvite.getFromFaction(), v -> new ArrayList<>()).add(fInvite);
        // add to byToCountry
        FactionInvite.byToCountry.computeIfAbsent(fInvite.getToCountry(), v -> new ArrayList<>()).add(fInvite);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent FactionInvite");

        if (sendMessage) {
            Faction faction = fInvite.getFromFactionFaction();

            // send message to invite sender
            Country fromCountry = fInvite.getFromCountryCountry();
            Country toCountry = fInvite.getToCountryCountry();

            ChatUtil.sendPrefixedMessage(fromCountry.getLeaderPlayerProfile().getOnlinePlayer(), "§aSent faction invite to country §f" + toCountry.getName() + "§a!");

            // send notif to recipient country leader
            Country factionLeader = faction.getLeaderCountry();

            // build message
            TextComponent.Builder message = Component.text();

            message.append(ChatUtil.mm.deserialize("<white>" + factionLeader.getName() + "<gold> has sent your country an invite to join their faction <white>" + faction.getName() + "<gold>!"))
                   .append(Component.newline())
                   // [Accept] button
                   .append(ChatUtil.mm.deserialize("<click:run_command:'/gc faction accept " + faction.getName() + "'>" +
                                                   "<hover:show_text:'<white>Click to accept the faction invite to " + faction.getName() + ".</white>'>" +
                                                   "<green><bold>[Accept]</bold></green>" +
                                                   "</hover></click>"
                   ))
                   .append(Component.text("  "))
                   // [Decline] button
                   .append(ChatUtil.mm.deserialize("<click:run_command:'/gc faction decline " + faction.getName() + "'>" +
                                                   "<hover:show_text:'<white>Click to decline the faction invite to " + faction.getName() + ".</white>'>" +
                                                   "<red><bold>[Decline]</bold></red>" +
                                                   "</hover></click>"));
            // send message to leader
            ChatUtil.sendPrefixedNotificationMessage(toCountry.getLeaderPlayerProfile().getOnlinePlayer(), message.build());
        }
    }

    // unsend a sent faction invite
    public static void unsend(FactionInvite fInvite) {
        if (fInvite == null)
            return;

        // remove from byUUID
        FactionInvite.byUUID.remove(fInvite.getUUID());
        // remove from byFromFaction
        List<FactionInvite> fInvites = FactionInvite.byFromFaction.get(fInvite.getFromFaction());
        if (fInvites != null) {
            fInvites.remove(fInvite);
            // delete entry if list is now empty
            if (fInvites.isEmpty())
                FactionInvite.byFromFaction.remove(fInvite.getFromFaction());
        }
        // remove from byToCountry
        fInvites = FactionInvite.byToCountry.get(fInvite.getToCountry());
        if (fInvites != null) {
            fInvites.remove(fInvite);
            // delete entry if list is now empty
            if (fInvites.isEmpty())
                FactionInvite.byToCountry.remove(fInvite.getToCountry());
        }

        FactionInvite.delete(fInvite, FactionInvite.all, FactionInvite.DISPLAY_NAME);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Unsent sent FactionInvite");
    }

    // accept a sent faction invite
    public static void accept(FactionInvite fInvite, boolean sendMessage) {
        if (fInvite == null)
            return;

        Country toCountry = fInvite.getToCountryCountry();

        unsend(fInvite);

        // don't do anything if already in faction
        if (toCountry.hasFaction())
            return;

        Faction faction = fInvite.getFromFactionFaction();
        if (faction == null)
            return;

        FactionService.joinFaction(toCountry, faction);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Accepted sent FactionInvite");

        if (sendMessage) {
            Player fromCountryLeader = fInvite.getFromCountryCountry().getLeaderPlayerProfile().getOnlinePlayer();
            if (fromCountryLeader == null)
                return;
            ChatUtil.sendPrefixedNotificationMessage(fromCountryLeader, """
                                                                        §6Your faction invite was §aaccepted§6.
                                                                        §f""" + toCountry.getName() + "§6 is now a member of your faction §f" + faction.getName() + "§6!");
        }
    }

    // decline a sent faction invite
    public static void decline(FactionInvite fInvite, boolean sendMessage) {
        if (fInvite == null)
            return;

        Country toCountry = fInvite.getToCountryCountry();

        unsend(fInvite);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Declined sent FactionInvite");

        if (sendMessage) {
            Player fromCountryLeader = fInvite.getFromCountryCountry().getLeaderPlayerProfile().getOnlinePlayer();
            if (fromCountryLeader == null)
                return;
            ChatUtil.sendPrefixedNotificationMessage(fromCountryLeader, "§6Your faction invite to country §f" + toCountry.getName() + "§6 was §cdeclined§6");
        }
    }

    // delete all faction invites sent by faction
    public static void deleteAllSentByFaction(Faction byFaction) {
        if (byFaction == null)
            return;

        List<FactionInvite> fInvitesSent = FactionInvite.byFromFaction.get(byFaction.getUUID());
        if (fInvitesSent == null)
            return;
        for (FactionInvite fInvite : new ArrayList<>(fInvitesSent)) {
            FactionInviteService.unsend(fInvite);
        }
    }

    // delete all faction invites received by country
    public static void deleteAllSentToCountry(Country toCountry) {
        if (toCountry == null)
            return;

        List<FactionInvite> fInvitesSent = FactionInvite.byToCountry.get(toCountry.getUUID());
        if (fInvitesSent == null)
            return;
        for (FactionInvite fInvite : new ArrayList<>(fInvitesSent)) {
            FactionInviteService.unsend(fInvite);
        }
    }

    public static boolean countryHasFactionInviteFromFaction(Faction fromFaction, Country toCountry) {
        if (fromFaction == null || toCountry == null)
            return false;

        List<FactionInvite> fInvites = FactionInvite.byFromFaction.get(fromFaction.getUUID());
        if (fInvites == null || fInvites.isEmpty())
            return false;

        return fInvites.stream().anyMatch(fi -> fi.getToCountry().equals(toCountry.getUUID()));
    }
}
