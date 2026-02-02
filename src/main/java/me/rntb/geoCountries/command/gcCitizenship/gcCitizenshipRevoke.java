package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

public class gcCitizenshipRevoke {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to revoke the citizenship of!");
            return;
        }

        PlayerProfile senderProfile = PlayerProfile.byCommandSender(sender);

        // if doesnt have citizenship or isn't leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.rank != PlayerProfile.PlayerRank.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to revoke a player's citizenship!");
            return;
        }

        // if revoking own citizenship, escape
        if (args[0].equals(senderProfile.username)) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't revoke your own citizenship, use §f/gc citizenship renounce§c instead!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.byUsername.get(args[0]);
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
                                  new Confirmation(gcCitizenshipRevoke::onConfirm,
                                                   sender,
                                                   new String[] { playerProfile.username }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byUsername.get(args[0]);

        Country country = playerProfile.getCitizenship();

        playerProfile.clearCitizenship();

        ChatUtil.sendPrefixedMessage(sender, "§aRevoked the citizenship of §f" + country.name + "§a!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.username + "§6 is no longer a citizen of §f" + country.name + "§6!", true);
    }
}
