package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubSubCommand;
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
import java.util.List;
import java.util.UUID;

public class gcCitizenshipApply extends SubSubCommand {

    public gcCitizenshipApply(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
        this.HelpString = "Applies for citizenship to a country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to apply to!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if already has citizenship, escape
        if (!ConfigState.debugMode && playerProfile.hasCitizenship()) {
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
            ChatUtil.sendPrefixedMessage(sender, "§cYou're already writing a citizenship application to §f" + cApplication.getToCountry().name + "§c!");
            return;
        }

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.uuid);
        if (cApplications != null) {
            // if sent too many applications, escape
            int cApplicationsCount = cApplications.size();
            if (cApplicationsCount >= ConfigState.maxCitizenshipApplications) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou've already sent too many §f(" + cApplicationsCount + "/" + ConfigState.maxCitizenshipApplications + ")§c citizenship applications! Unsend one by doing §f/gc citizenship unsend [country]");
                return;
            }
            // if already sent application to this country, escape
            if (!ConfigState.debugMode && cApplications.stream().anyMatch(ca -> ca.toCountry.equals(toCountry.uuid))) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou already have a pending citizenship application to §f" + countryName + "§c!");
                return;
            }
        }

        // create new application
        UUID playerUUID = playerProfile.uuid;
        cApplication = new CitizenshipApplication(UUID.randomUUID(),
                                                  playerUUID,
                                                  toCountry.uuid); // reuse variable
        cApplication.reason = "N/A";
        cApplication.open(true);

        // if country has auto-accept enabled, accept and escape
        if (toCountry.settings.get("autoacceptcitizenshipapplications").equals("true")) {
            cApplication.cancel(false); // cancel open application
            cApplication.accept(true); // send sent application
            // broadcast notif to country
            ChatUtil.broadcastPrefixedMessageToCountry(toCountry, "§f" + playerProfile.username + "§6 is now a citizen of §f" + toCountry.name + "§6!", false);
            return;
        }

        ChatUtil.sendPrefixedMessage(sender, "§6What is your reason for applying for citizenship of §f" + countryName + "§6?");

        // start waiting for response
        Response.startWaiting(playerUUID,
                              new Response(this::onResponse,
                                           sender),
                              true);
    }

    @Override
    public void onResponse(CommandSender sender, String response) {
        String responseClean = response.trim();

        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(((Player) sender).getUniqueId());

        // validate response
        String validation = StringUtil.validateResponse(responseClean);
        if (validation != null) {
            cApplication.cancel(true);
            ChatUtil.sendPrefixedMessage(sender, validation);
            return;
        }

        cApplication.reason = responseClean;

        // send application
        cApplication.send(true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsStrings(true) : List.of();
    }
}
