package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.service.FactionService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcFactionKick extends GeoCommand {

    public gcFactionKick(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Kicks a member country out of your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Faction faction = playerProfile.getFactionObject();
        // if doesnt have faction, escape
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a faction to kick people from it!");
            return;
        }

        Country senderCountry = playerProfile.getCitizenshipObject();
        // if not leader of faction, escape
        if (!senderCountry.isFactionLeader()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the faction to kick people!");
            return;
        }

        String countryName = String.join(" ", args);

        // if kicking own country, escape
        if (countryName.equals(senderCountry.getName())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't kick yourself from your own faction!");
            return;
        }

        Country country = Country.get(countryName);
        // if player not exist, escape
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c could not be found!");
            return;
        }

        // if country is not member of sender's faction, escape
        if (country.getFaction() == null || !country.getFaction().equals(faction.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c must be a member of your faction!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { countryName }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        Country country = Country.get(args[0]);

        Faction faction = country.getFactionObject();

        FactionService.leaveFaction(country);

        ChatUtil.sendPrefixedMessage(sender, "§aKicked country §f" + country.getName() + "§a from faction!");

        // broadcast notif to faction
        ChatUtil.broadcastPrefixedMessageToFaction(faction, "§6Member country §f" + country.getName() + "§6 was just kicked out of your faction!", false);

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6Your country was just kicked from the faction §3" + faction.getName() + "§6!", true);
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getFactionObject();
        // should never trigger!
        if (faction == null)
            return null;

        List<Country> members = faction.getMembers().stream()
                                                    .filter(uuid -> !uuid.equals(playerProfile.getCitizenshipObject().getUUID()))
                                                    .map(Country::get).toList();

        return MenuPage.createSkullMenuButtons(members, member -> member.getLeaderObject().getOfflinePlayer(),
                                                        member -> "§a" + member.getName(),
                                                        member -> "Kick §6" + member.getName() + "§f from the faction",
                                                        member -> "gc faction kick " + member.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getFactionObject();
        if (faction == null)
            return List.of();
        Country country = faction.getLeaderObject();
        if (country == null)
            return List.of();
        // goo goo ga ga
        return faction.getMembers().stream()
                                   .filter(uuid -> !uuid.equals(country.getUUID()))
                                   .map(c -> Country.get(c).getName()).toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country country = playerProfile.getCitizenshipObject();
        if (country == null)
            return false;
        Faction faction = country.getFactionObject();
        if (faction == null)
            return false;
        return playerProfile.getPosition() == Position.LEADER && country.isFactionLeader() && faction.getMemberCount() > 1; // if 1, leader is the only member
    }
}
