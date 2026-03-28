package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.FactionService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.ValidationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcFactionCreate extends GeoCommand {

    public gcFactionCreate(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Creates a new faction.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Country country = playerProfile.getCitizenshipObject();
        // doesn't have country
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to create a faction!");
            return;
        }
        // is not leader
        if (playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to create a faction!");
            return;
        }

        // already has faction
        if (country.hasFaction()) {
            // must transfer ownership then leave
            if (playerProfile.getPosition() == Position.LEADER)
                ChatUtil.sendPrefixedMessage(sender, "§cYou must first transfer leadership of your current faction §3" + country.getFactionObject().getName() + "§c using §f/gc faction transfer§c, then leave it using §f/gc faction leave§c before you can create a faction!");
            // must leave faction
            else
                ChatUtil.sendPrefixedMessage(sender, "§cYou must first leave your current faction §3" + country.getFactionObject().getName() + "§c §c/gc faction leave§f before you can create a faction!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What do you want the name of your new faction to be?");
            // start waiting for response
            Response.startWaiting(playerProfile.getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String factionName = String.join(" ", args);
            onResponse(sender, factionName);
        }
    }

    private void onResponse(CommandSender sender, String factionName) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        factionName = factionName.trim();
        // validation check
        String validationString = ValidationUtil.validateFactionName(factionName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        Country country = playerProfile.getCitizenshipObject();
        Faction newFaction = new Faction(UUID.randomUUID(), factionName, country.getUUID());

        // create faction
        newFaction.register();
        FactionService.joinFaction(country, newFaction);
        FactionService.promoteToLeader(country);

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aCreated faction §3" + factionName + "§a!");

        ChatUtil.broadcastPrefixedMessage("§6A new faction §3" + factionName + "§6 has just been created!");
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return !playerProfile.hasFaction() && playerProfile.getPosition() == Position.LEADER;
    }
}
