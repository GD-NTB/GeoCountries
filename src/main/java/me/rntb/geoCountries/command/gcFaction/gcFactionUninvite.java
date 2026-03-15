package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.*;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class gcFactionUninvite extends GeoCommand {

    public gcFactionUninvite(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Uninvites a previously invited country to your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What country was previously invited that you now want to uninvite?");
            // start waiting for response
            Response.startWaiting(playerProfile.getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args);
            onResponse(sender, countryName);
        }

    }

    private void onResponse(CommandSender sender, String countryName) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
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

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        Country country = PlayerProfile.get(sender).getCitizenshipCountry();
        if (country == null)
            return List.of();
        UUID factionUUID = country.getFaction();
        if (factionUUID == null)
            return List.of();

        List<FactionInvite> fInvitesSent = FactionInvite.byFromFaction.get(factionUUID);
        if (fInvitesSent == null)
            return List.of();

        return fInvitesSent.stream()
                           .map(fi -> fi.getToCountryCountry().getName()).toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return !getTabCompletion(sender, new String[] { "" }).isEmpty();
    }
}
