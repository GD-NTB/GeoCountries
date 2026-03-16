package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
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
import java.util.function.Predicate;

public class gcFactionUninvite extends GeoCommand {

    public gcFactionUninvite(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Uninvites a previously invited country to your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to uninvite from your faction!");
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

        ArrayList<FactionInvite> fInvites = FactionInvite.byFromFaction.get(playerProfile.getCitizenshipCountry().getFaction());

        // if hasnt sent any pending faction invites, escape
        if (fInvites == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't sent a faction invite to §f" + countryName + "§c!");
            return;
        }

        FactionInvite fInvite = fInvites.stream()
                                        .filter(ca -> ca.getToCountry().equals(toCountry.getUUID()))
                                        .findFirst().orElse(null);

        // if havent sent faction invite to this country, escape
        if (fInvite == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't sent a faction invite to §f" + countryName + "§c!");
            return;
        }

        FactionInviteService.unsend(fInvite);

        ChatUtil.sendPrefixedMessage(sender, "§aUnsent faction invite!");
    }

    private Predicate<Country> includeCountryPredicate(Faction faction) {
        return (c) -> FactionInviteService.countryHasFactionInviteFromFaction(faction, c);
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        Faction faction = PlayerProfile.get(sender).getCitizenshipCountry().getFactionFaction();
        // should never trigger!
        if (faction == null)
            return null;

        List<Country> validCountries;
        List<FactionInvite> fInvites = FactionInvite.byFromFaction.get(faction.getUUID());
        if (fInvites == null || fInvites.isEmpty())
            validCountries = Country.all;
        else
            validCountries = fInvites.stream()
                                     .map(FactionInvite::getToCountryCountry).toList();

        return MenuPage.createSkullMenuButtons(validCountries, country -> country.getLeaderPlayerProfile().getOfflinePlayer(),
                                                               country -> "§a" + country.getName(),
                                                               country -> "Uninvite §6" + country.getName(),
                                                               country -> "gc faction uninvite " + country.getName());
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
        if (faction == null || !faction.getLeader().equals(playerProfile.getCitizenship()))
            return false;

        List<FactionInvite> fInvites = FactionInvite.byFromFaction.get(faction.getUUID());
        return fInvites != null && !fInvites.isEmpty();
    }
}
