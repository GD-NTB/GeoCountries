package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class gcFactionInvite extends GeoCommand {

    public gcFactionInvite(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Invites a country to your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to invite to your faction!");
            return;
        }

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

        String countryName = String.join(" ", args);
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

        ArrayList<FactionInvite> fInvites = FactionInvite.byFromFaction.get(faction.getUUID());
        if (fInvites != null) {
            // if sent too many applications, escape
            int cApplicationsCount = fInvites.size();
            if (ConfigState.maxFactionInvites != -1 && cApplicationsCount >= ConfigState.maxFactionInvites) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou've already sent too many §f(" + cApplicationsCount + "/" + ConfigState.maxFactionInvites + ")§c faction invites! Unsend one by doing §f/gc faction unsend [country]");
                return;
            }
            // if already sent application to this country, escape
            if (fInvites.stream().anyMatch(ca -> ca.getToCountry().equals(toCountry.getUUID()))) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou already have a pending faction invite to §f" + countryName + "§c!");
                return;
            }
        }

        // create new invite and send
        FactionInvite factionInvite = new FactionInvite(UUID.randomUUID(),
                                                        faction.getUUID(),
                                                        playerProfile.getCitizenshipCountry().getUUID(),
                                                        toCountry.getUUID());
        FactionInviteService.send(factionInvite, true);

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Sent faction invite from " + faction.getName());
    }

    private Predicate<Country> includeCountryPredicate(Faction faction) {
        return (c) -> !c.hasFaction() && !faction.getMembers().contains(c.getUUID()) && !FactionInviteService.countryHasFactionInviteFromFaction(faction, c);
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        Faction faction = PlayerProfile.get(sender).getCitizenshipCountry().getFactionFaction();
        if (faction == null)
            return null;

        return Country.getAllAsMenuButtons(includeCountryPredicate(faction),
                                           (c) -> "§fInvite §6" + c.getName() + "§f to your faction.",
                                           (c) -> "gc faction invite " + c.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.getCitizenship() == null)
            return List.of();
        Faction faction = playerProfile.getCitizenshipCountry().getFactionFaction();
        if (faction == null)
            return List.of();

        return Country.all.stream()
                          .filter(includeCountryPredicate(faction))
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
