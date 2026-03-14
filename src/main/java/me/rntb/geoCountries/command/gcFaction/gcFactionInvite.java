package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class gcFactionInvite extends GeoCommand {

    public gcFactionInvite(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Invites a country to your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if not in country, escape
        Country country = playerProfile.getCitizenshipCountry();
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are not the leader of a faction!");
            return;
        }

        Faction faction = country.getFactionFaction();
        // if not in faction, escape
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are not the leader of a faction!");
            return;
        }
        // if not leader of faction, escape
        if (playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are not the leader of your faction!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What country do you want to invite to your faction?");
            // start waiting for response
            Response.startWaiting(playerProfile.getUUID(),
                                  new Response(this::onResponseCountryName,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args);
            onResponseCountryName(sender, countryName);
        }
    }

    private void onResponseCountryName(CommandSender sender, String countryName) {
        Country toCountry = Country.get(countryName);
        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }
        // if country is already in faction
        if (toCountry.hasFaction()) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c is already in faction §f " + toCountry.getFactionFaction().getName() + "§c!");
            return;
        }
        // if leader of country not online, escape
        if (!toCountry.isLeaderOnline()) {
            ChatUtil.sendPrefixedMessage(sender, "§cThe leader §f" + toCountry.getLeaderPlayerProfile().getUsername() + "§c of that country is not online!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getCitizenshipCountry().getFactionFaction();
        PlayerProfile leader = toCountry.getLeaderPlayerProfile();

        // create new invite and send
        FactionInvite factionInvite = new FactionInvite(UUID.randomUUID(),
                                                        faction.getUUID(),
                                                        playerProfile.getCitizenshipCountry().getUUID(),
                                                        toCountry.getUUID());
        FactionInviteService.send(factionInvite, true);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent faction invite from " + faction.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();
        return Country.all.stream()
                          .filter(c -> !c.hasFaction())
                          .map(Country::getName)
                          .toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.getPosition() != PlayerProfile.Position.LEADER)
            return false;
        Faction faction = playerProfile.getCitizenshipCountry().getFactionFaction();
        return faction != null && faction.getLeader().equals(playerProfile.getCitizenship());
    }
}
