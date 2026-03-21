package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionInviteService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcFactionDecline extends GeoCommand {

    public gcFactionDecline(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Declines a player's invite to their faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the faction you want to decline the faction invite of!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if not leader, escape
        if (playerProfile.getPosition() != Position.LEADER && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can decline faction invites!");
            return;
        }
        Country country = playerProfile.getCitizenshipCountry();

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
        FactionInviteService.decline(fInvite, true);

        ChatUtil.sendPrefixedMessage(sender, "§aDeclined §3" + factionName + "§a's citizenship application.");
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
