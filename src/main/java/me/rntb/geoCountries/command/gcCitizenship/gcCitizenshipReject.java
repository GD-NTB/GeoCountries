package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCitizenshipReject extends GeoCommand {

    public gcCitizenshipReject(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Rejects a player's citizenship application to your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to reject the citizenship application of!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if not leader, escape
        if (playerProfile.getPosition() != Position.LEADER && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can reject citizenship applications!");
            return;
        }

        // get player
        String otherPlayerName = args[0];
        PlayerProfile otherPlayer = PlayerProfile.get(otherPlayerName);
        if (otherPlayer == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c could not be found!");
            return;
        }

        // get country
        Country country = playerProfile.getCitizenshipObject();
        if (otherPlayer.getCitizenship() != null && otherPlayer.getCitizenship().equals(country.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c is already a citizen of your country!");
            return;
        }

        // get citizenship applications sent by other player
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(otherPlayer.getUUID());
        if (cApplications == null || cApplications.isEmpty()) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
            return;
        }

        // get the citizenship application to the sender's country
        CitizenshipApplication cApplication = cApplications.stream()
                                                           .filter(ca -> ca.getToCountry().equals(playerProfile.getCitizenship()))
                                                           .findFirst().orElse(null);
        if (cApplication == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
            return;
        }

        // reject the application
        CitizenshipApplicationService.reject(cApplication, true);

        ChatUtil.sendPrefixedMessage(sender, "§aRejected §f" + otherPlayerName + "§a's citizenship application.");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (playerProfile.getPosition() != Position.LEADER)
            return List.of();

        return playerProfile.getCitizenshipObject().getReceivedCitizenshipApplicationsAsUsernames();
    }
}
