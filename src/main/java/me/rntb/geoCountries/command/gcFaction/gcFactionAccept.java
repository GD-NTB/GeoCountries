package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.*;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcFactionAccept extends GeoCommand {

    public gcFactionAccept(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Accepts a player's invite to their faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the faction you want to accept the faction invite of!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if not leader, escape
        if (playerProfile.getPosition() != Position.LEADER && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can accept faction invites!");
            return;
        }
        Country country = playerProfile.getCitizenshipCountry();
        if (country.hasFaction()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are already in a faction!");
            return;
        }

        // get faction
        String factionName = String.join(" ", args);
        Faction faction = Faction.get(factionName);
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cFaction §3" + factionName + "§c could not be found!");
            return;
        }

        // get faction invites sent by other player
        List<FactionInvite> fInvites = FactionInvite.byToCountry.get(country.getUUID());
        if (fInvites == null || fInvites.isEmpty()) {
            ChatUtil.sendPrefixedMessage(sender, "§cFaction §3" + factionName + "§c has not sent a faction invite to your country!");
            return;
        }

        // get the faction invite to the sender's country
        FactionInvite fInvite = fInvites.stream()
                                         .filter(fi -> fi.getToCountry().equals(country.getUUID()))
                                         .findFirst().orElse(null);
        if (fInvite == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cFaction §3" + factionName + "§c has not sent a faction invite to your country!");
            return;
        }

        // accept the application
        FactionInviteService.accept(fInvite, true);

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aAccepted the faction invite!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + country.getName() + "§6 is now a member of your faction §f" + faction.getName() + "§6!", false);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (playerProfile.getPosition() != Position.LEADER)
            return List.of();

        List<FactionInvite> fInvites = FactionInvite.byToCountry.get(playerProfile.getCitizenshipCountry().getUUID());
        if (fInvites == null)
            return List.of();
        return fInvites.stream()
                       .map(fi -> fi.getFromFactionFaction().getName()).toList();
    }
}
