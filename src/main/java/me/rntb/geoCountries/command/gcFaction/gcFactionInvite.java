package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c is already in faction §f" + toCountry.getFactionFaction().getName() + "§c!");
            return;
        }

        ArrayList<FactionInvite> fInvites = FactionInvite.byFromFaction.get(faction.getUUID());
        if (fInvites != null) {
            // if sent too many applications, escape
            int cApplicationsCount = fInvites.size();
            if (ConfigState.maxFactionInvites != -1 && cApplicationsCount >= ConfigState.maxFactionInvites) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou've already sent too many §f(" + cApplicationsCount + "/" + ConfigState.maxFactionInvites + ")§c faction invites! Unsend one by doing §f/gc faction uninvite [country]");
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

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        Faction faction = PlayerProfile.get(sender).getFaction();
        // should never trigger!
        if (faction == null)
            return null;

        List<Country> countries = Country.all.stream()
                                             .filter((c) -> !c.hasFaction() &&
                                                            !FactionInviteService.countryHasFactionInviteFromFaction(faction, c) &&
                                                            !faction.getMembers().contains(c.getUUID()))
                                             .toList();

        return MenuPage.createSkullMenuButtons(countries, country -> country.getLeaderPlayerProfile().getOfflinePlayer(),
                                                          country -> "§a" + country.getName(),
                                                          country -> "Invite §6" + country.getName() + "§f to your faction",
                                                          country -> "gc faction invite " + country.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getFaction();
        if (faction == null)
            return List.of();

        return Country.all.stream()
                          .filter((c) -> !c.hasFaction() &&
                                         !FactionInviteService.countryHasFactionInviteFromFaction(faction, c) &&
                                         !faction.getMembers().contains(c.getUUID()))
                          .map(Country::getName)
                          .toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).isFactionLeader();
    }
}
