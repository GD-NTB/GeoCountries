package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import me.rntb.geoCountries.util.ValidationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFactionRename extends GeoCommand {

    public gcFactionRename(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Renames your faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Faction faction = playerProfile.getFactionObject();
        // if doesnt have faction, escape
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a faction to rename it!");
            return;
        }

        // if not leader of faction, escape
        if (playerProfile.getPosition() != Position.LEADER && faction.getLeader().equals(playerProfile.getCitizenship())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the faction to change its name!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What do you want the new name of your faction to be?");
            // start waiting for response
            Response.startWaiting(playerProfile.getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String factionName = String.join(" ", args).trim();
            onResponse(sender, factionName);
        }
    }

    private void onResponse(CommandSender sender, String factionName) {
        // validation check
        String validationString = ValidationUtil.validateFactionName(factionName, true);
        if (validationString != null) {
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { factionName }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        String factionName = args[0];
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Faction faction = playerProfile.getFactionObject();

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aRenamed faction to §3" + factionName + "§a!");

        ChatUtil.broadcastPrefixedMessage("§6The faction §3" + faction.getName() + "§6 has been renamed to §3" + factionName + "§6!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToFaction(faction, "§6Your faction has now been renamed to §3" + factionName + "§6!", true);

        faction.setName(factionName);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).isFactionLeader();
    }
}
