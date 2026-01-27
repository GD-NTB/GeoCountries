package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class gcCitizenshipApply {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to apply to!");
            return;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);

        // if already has citizenship, escape
        if (!ConfigState.DebugMode) {
            if (playerProfile.hasCitizenship()) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou can't apply for citizenship of country whilst being a citizen of another!");
                return;
            }
        }

        String countryName = String.join(" ", args);
        Country toCountry = Country.byName.get(countryName);

        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        // if already has open application, escape
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(playerProfile.uuid);
        if (cApplication != null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou're already writing a citizenship application to §f" + cApplication.getToCountry().name + "§c!");
            return;
        }

        // if has pending application to this country, escape
        if (!ConfigState.DebugMode) {
            ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.uuid);
            if (cApplications != null && cApplications.stream().anyMatch(ca -> ca.toCountry.equals(toCountry.uuid))) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou already have a pending citizenship application to §f" + countryName + "§c!");
                return;
            }
        }

        // create new application
        UUID playerUUID = playerProfile.uuid;
        cApplication = new CitizenshipApplication(UUID.randomUUID(),
                                                  playerUUID,
                                                  toCountry.uuid); // reuse variable
        CitizenshipApplication.open(cApplication,true);



        ChatUtil.sendPrefixedMessage(sender, "§6What is your reason for applying for citizenship of §f" + countryName + "§6?");

        // start waiting for response
        Response.startWaiting(playerUUID,
                              new Response(gcCitizenshipApply::onResponse,
                                           sender),
                              true);
    }

    private static void onResponse(CommandSender sender, String response) {
        String responseClean = response.trim();

        // validate response
        String validation = StringUtil.validateResponse(responseClean);
        if (validation != null) {
            ChatUtil.sendPrefixedMessage(sender, validation);
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(uuid);

        cApplication.reason = responseClean;

        // send application
        CitizenshipApplication.send(cApplication, true);
    }
}
