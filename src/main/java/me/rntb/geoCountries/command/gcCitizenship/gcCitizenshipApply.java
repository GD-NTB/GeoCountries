package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.types.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class gcCitizenshipApply {

    public static void doCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to apply to!");
            return;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());

        // if already has citizenship, escape
        if (playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't apply for citizenship of country whilst being a citizen of another!");
            return;
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
            ChatUtil.sendPrefixedMessage(sender, "§cYou're already writing a citizenship application to §f" + cApplication.getCountry().name + "§c!");
            return;
        }

        // if sent application to this country before, escape
        cApplication = CitizenshipApplication.sentByApplicant.get(playerProfile.uuid); // reuse variable
        if (cApplication != null && cApplication.toCountry.equals(toCountry.uuid)) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou already have a pending citizenship application for §f" + countryName + "§c!");
            return;
        }

        // create new application
        UUID playerUUID = playerProfile.uuid;
        cApplication = new CitizenshipApplication(UUID.randomUUID(),
                                                  playerUUID,
                                                  toCountry.uuid); // reuse variable
        CitizenshipApplication.open(cApplication,true);



        ChatUtil.sendPrefixedMessage(sender, "§6Why are you applying to have citizenship of §f" + countryName + "§6?");

        // start waiting for response
        Response.startWaiting(playerUUID,
                              new Response(gcCitizenshipApply::onResponse,
                                           sender),
                              true);
    }

    private static void onResponse(CommandSender sender, String response) {
        String responseClean = response.trim();

        // validate response
        String validation = StringUtil.ValidateResponse(responseClean);
        if (validation != null) {
            ChatUtil.sendPrefixedMessage(sender, validation);
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(uuid);

        cApplication.reason = responseClean;

        ChatUtil.sendPrefixedMessage(sender, "§6Are you sure you want to send this citizenship application?");
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcCitizenshipApply::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(uuid);

        CitizenshipApplication.send(cApplication, true);
    }
}
