package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.PlayerRank;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCitizenshipRevoke extends GeoCommand {

    public gcCitizenshipRevoke(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Removes the citizenship of a player of your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to revoke the citizenship of!");
            return;
        }

        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship or isn't leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.rank != PlayerRank.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to revoke a player's citizenship!");
            return;
        }

        // if revoking own citizenship, escape
        if (args[0].equals(senderProfile.username)) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't revoke your own citizenship, use §f/gc citizenship renounce§c instead!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(args[0]);
        // if player not exist, escape
        if (playerProfile == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + args[0] + "§c could not be found!");
            return;
        }

        // if player is not citizen of sender's country, escape
        if (!playerProfile.citizenship.equals(senderProfile.citizenship)) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerProfile.username + "§c is not a citizen of your country!");
            return;
        }

        // todo: check if we are revoking leader inheritor citizenship, that shouldnt happen!

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { playerProfile.username }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(args[0]);

        Country country = player.getCitizenship();

        CitizenshipService.leaveCountry(player);

        ChatUtil.sendPrefixedMessage(sender, "§aRevoked the citizenship of §f" + country.name + "§a!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + player.username + "§6 is no longer a citizen of §f" + country.name + "§6!", true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        Country country = PlayerProfile.get(sender).getCitizenship();
        if (country == null)
            return List.of();

        return country.citizensAsStrings();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile player = PlayerProfile.get(sender);
        return player.rank == PlayerRank.LEADER && player.getCitizenship().citizenCount() > 1; // if 1, leader is the only citizen
    }
}
