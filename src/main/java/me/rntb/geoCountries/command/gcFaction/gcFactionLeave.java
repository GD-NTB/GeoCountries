package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFactionLeave extends GeoCommand {

    public gcFactionLeave(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Leaves your current faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Faction faction = playerProfile.getFaction();
        // if doesnt have faction, escape
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be in a faction in order to leave it!");
            return;
        }

        // if leader of faction, print message
        if (faction.getLeader().equals(playerProfile.getCitizenship()) && playerProfile.getPosition() == Position.LEADER)
            ChatUtil.sendPrefixedMessage(sender, "§6You are the leader of the faction - leaving it will disband the entire faction. Are you sure you want to continue?");

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Faction faction = playerProfile.getFaction();


        // if leader of faction, disband faction
        if (faction.getLeader().equals(playerProfile.getCitizenship()) && playerProfile.getPosition() == Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§aDisbanded faction §f" + faction.getName() + "§a!");
            FactionService.disband(faction);
        }
        // else leave and broadcast notif to faction
        else {
            ChatUtil.sendPrefixedNotificationMessage(sender, "§aLeft the faction §f" + faction.getName() + "§a!");
            FactionService.leaveFaction(playerProfile.getCitizenshipCountry());

            ChatUtil.broadcastPrefixedMessageToFaction(faction, "§f" + playerProfile.getUsername() + "§6 is no longer a member of §f" + faction.getName() + "§6!", false);
        }
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return playerProfile.hasFaction();
    }
}
