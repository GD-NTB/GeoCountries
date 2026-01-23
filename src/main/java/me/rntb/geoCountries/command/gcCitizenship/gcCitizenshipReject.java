package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class gcCitizenshipReject {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to reject the citizenship application of!");
            return;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);

        // if not leader, escape
        if (playerProfile.rank != PlayerProfile.PlayerRank.LEADER && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can reject citizenship applications!");
            return;
        }

        // get player
        String otherPlayerName = args[0];
        PlayerProfile otherPlayer = PlayerProfile.byUsername.get(otherPlayerName);
        if (otherPlayer == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c could not be found!");
            return;
        }

        // get country
        Country country = Country.byUUID.get(playerProfile.citizenship);
        if (otherPlayer.citizenship != null && otherPlayer.citizenship.equals(country.uuid)) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c is already a citizen of your country!");
            return;
        }

        // get citizenship applications sent by other player
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(otherPlayer.uuid);
        if (cApplications == null || cApplications.isEmpty()) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
            return;
        }

        // get the citizenship application to the sender's country
        CitizenshipApplication cApplication = cApplications.stream()
                                                           .filter(ca -> ca.toCountry.equals(playerProfile.citizenship))
                                                           .findFirst().orElse(null);
        if (cApplication == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
            return;
        }

        // reject the application
        CitizenshipApplication.reject(cApplication, true);

        ChatUtil.sendPrefixedMessage(sender, "§aRejected §f" + otherPlayerName + "§a's citizenship application.");
    }
}
