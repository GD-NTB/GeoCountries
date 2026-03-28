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

public class gcFactionTransfer extends GeoCommand {

    public gcFactionTransfer(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Transfers the leadership of your faction to a member country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Faction faction = playerProfile.getFactionObject();
        // if doesnt have faction, escape
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a faction to transfer its leadership!");
            return;
        }

        Country senderCountry = playerProfile.getCitizenshipObject();
        // if not leader of faction, escape
        if (!senderCountry.isFactionLeader()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the faction to transfer leadership!");
            return;
        }

        String countryName = String.join(" ", args);

        // if transferring to own country, escape
        if (countryName.equals(senderCountry.getName())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't transfer leadership to your own country, obviously!");
            return;
        }

        Country newLeader = Country.get(countryName);
        // if player not exist, escape
        if (newLeader == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c could not be found!");
            return;
        }

        // if country is not member of sender's faction, escape
        if (newLeader.getFaction() == null || !newLeader.getFaction().equals(faction.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c must be a member of your faction before you can transfer leadership to it!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { countryName }),
                                  true);

    }

    // todo: theres bugs all over the place if the country/faction gets deleted before confirming, do null checks
    private void onConfirm(CommandSender sender, String[] args) {
        Country newLeader = Country.get(args[0]);

        Faction faction = newLeader.getFactionObject();

        FactionService.promoteToLeader(newLeader);

        ChatUtil.sendPrefixedMessage(sender, "§aTransferred faction leadership to §f" + newLeader.getName() + "§a! §cYour country is no longer the leader.");

        // broadcast notif to faction
        ChatUtil.broadcastPrefixedMessageToFaction(faction, "§6The leadership of your faction has changed, the new leader of §3" + faction.getName() + "§6 is §f" + newLeader.getName() + "§6!", true);

        // send notif to new leader
        PlayerProfile newLeaderPlayerProfile = newLeader.getLeaderObject();
        if (newLeaderPlayerProfile == null)
            return;
        ChatUtil.sendPrefixedMessage(newLeaderPlayerProfile.getOnlinePlayer(), "§6Your country is now the new leader of §3" + faction.getName() + "§6!");
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
                                                        member -> "Transfer leadership to §6" + member.getName(),
                                                        member -> "gc faction transfer " + member.getName());
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
