package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.PlayerRank;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCitizenshipAccept extends GeoCommand {

    public gcCitizenshipAccept(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Accepts a player's citizenship application to your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to accept the citizenship application of!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if not leader, escape
        if (playerProfile.rank != PlayerRank.LEADER && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can accept citizenship applications!");
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

        // accept the application
        CitizenshipApplicationService.accept(cApplication, true);


        ChatUtil.sendPrefixedNotificationMessage(sender, "§aAccepted the citizenship application!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.username + "§6 is now a citizen of §f" + country.name + "§6!", false);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (player.rank != PlayerRank.LEADER)
            return List.of();

        return player.getCitizenship().getReceivedCitizenshipApplicationsAsStrings();
    }
}
