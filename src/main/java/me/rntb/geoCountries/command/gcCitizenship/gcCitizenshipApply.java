package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class gcCitizenshipApply extends GeoCommand {

    public gcCitizenshipApply(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Applies for citizenship to a country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        // if already has citizenship, escape
        if (!ConfigState.debugMode && player.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't apply for citizenship of country whilst being a citizen of another!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What country do you want to apply for citizenship to?");
            // start waiting for response
            Response.startWaiting(player.uuid,
                                  new Response(this::onResponseCountryName,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args);
            onResponseCountryName(sender, countryName);
        }
    }

    private void onResponseCountryName(CommandSender sender, String countryName) {
        PlayerProfile player = PlayerProfile.get(sender);

        Country toCountry = Country.get(countryName);
        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            ChatUtil.sendPrefixedMessage(sender, "§aCancelled the citizenship application!");
            return;
        }

        // if already has open application, escape
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(player.uuid);
        if (cApplication != null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou're already writing a citizenship application to §f" + cApplication.getToCountry().name + "§c!");
            ChatUtil.sendPrefixedMessage(sender, "§aCancelled the citizenship application!");
            return;
        }

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.uuid);
        if (cApplications != null) {
            // if sent too many applications, escape
            int cApplicationsCount = cApplications.size();
            if (ConfigState.maxCitizenshipApplications != -1 && cApplicationsCount >= ConfigState.maxCitizenshipApplications) {
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
        UUID playerUUID = player.uuid;
        cApplication = new CitizenshipApplication(UUID.randomUUID(),
                                                  playerUUID,
                                                  toCountry.uuid); // reuse variable
        cApplication.reason = "N/A";
        CitizenshipApplicationService.open(cApplication, true);

        // if country has auto-accept enabled, accept and escape
        if (toCountry.settings.get("autoacceptcitizenshipapplications").equals("true")) {
            CitizenshipApplicationService.cancel(cApplication, false); // cancel open application
            CitizenshipApplicationService.accept(cApplication, true); // send sent application
            // broadcast notif to country
            ChatUtil.broadcastPrefixedMessageToCountry(toCountry, "§f" + player.username + "§6 is now a citizen of §f" + toCountry.name + "§6!", false);
            return;
        }

        ChatUtil.sendPrefixedMessage(sender, "§6What is your reason for applying for citizenship of §f" + countryName + "§6?");

        // start waiting for response
        Response.startWaiting(playerUUID,
                              new Response(this::onResponseReason,
                                           sender),
                              true);

    }

    private void onResponseReason(CommandSender sender, String response) {
        String responseClean = response.trim();

        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(((Player) sender).getUniqueId());

        // validate response
        String validation = StringUtil.validateResponse(responseClean);
        if (validation != null) {
            CitizenshipApplicationService.cancel(cApplication, true);
            ChatUtil.sendPrefixedMessage(sender, validation);
            return;
        }

        cApplication.reason = responseClean;

        // send application
        CitizenshipApplicationService.send(cApplication, true);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsStrings(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile player = PlayerProfile.get(sender);
        if (player.hasCitizenship())
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.uuid);
        return cApplications == null || cApplications.size() < ConfigState.maxCitizenshipApplications;
    }
}
