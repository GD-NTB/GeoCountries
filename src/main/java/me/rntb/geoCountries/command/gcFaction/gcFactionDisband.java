package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFactionDisband extends GeoCommand {

    public gcFactionDisband(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Disbands (deletes) your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if not in country, escape
        Country country = playerProfile.getCitizenshipObject();
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are not the leader of a faction!");
            return;
        }

        Faction faction = country.getFactionObject();
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

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[0]),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getFactionObject();

        ChatUtil.sendPrefixedMessage(sender, "§aDisbanded faction §3" + faction.getName() + "§a!");

        FactionService.disband(faction);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).isFactionLeader();
    }
}
