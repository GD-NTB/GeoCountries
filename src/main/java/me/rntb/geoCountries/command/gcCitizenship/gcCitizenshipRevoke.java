package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCitizenshipRevoke extends GeoCommand {

    public gcCitizenshipRevoke(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Removes the citizenship of a player of your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship or isn't leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to revoke a player's citizenship!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What is the name of the player you want to revoke the citizenship of?");
            // start waiting for response
            Response.startWaiting(senderProfile.getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else
            onResponse(sender, args[0]);
    }

    private void onResponse(CommandSender sender, String playerName) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if revoking own citizenship, escape
        if (playerName.equals(senderProfile.getUsername())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't revoke your own citizenship, use §f/gc citizenship renounce§c instead!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(playerName);
        // if player not exist, escape
        if (playerProfile == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // if player is not citizen of sender's country, escape
        if (!playerProfile.getCitizenship().equals(senderProfile.getCitizenship())) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerProfile.getUsername() + "§c is not a citizen of your country!");
            return;
        }

        // todo: check if we are revoking leader inheritor citizenship, that shouldnt happen!

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { playerProfile.getUsername() }),
                                  true);

    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(args[0]);

        Country country = playerProfile.getCitizenshipCountry();

        CitizenshipService.leaveCountry(playerProfile);

        ChatUtil.sendPrefixedMessage(sender, "§aRevoked the citizenship of §f" + country.getName() + "§a!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.getUsername() + "§6 is no longer a citizen of §f" + country.getName() + "§6!", true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        Country country = PlayerProfile.get(sender).getCitizenshipCountry();
        if (country == null)
            return List.of();

        return country.getCitizensAsStrings();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return playerProfile.getPosition() == Position.LEADER && playerProfile.getCitizenshipCountry().getCitizenCount() > 1; // if 1, leader is the only citizen
    }
}
