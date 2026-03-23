package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class gcCitizenshipApply extends GeoCommand {

    public gcCitizenshipApply(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Applies for citizenship to a country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to apply to get citizenship of!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if already has citizenship, escape
        if (!ConfigState.debugMode && playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't apply for citizenship of country whilst being a citizen of another!");
            return;
        }

        String countryName = String.join(" ", args);

        Country toCountry = Country.get(countryName);
        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            ChatUtil.sendPrefixedMessage(sender, "§aCancelled the citizenship application!");
            return;
        }

        // if already has open application, escape
        CitizenshipApplication cApplication = CitizenshipApplication.openByApplicant.get(playerProfile.getUUID());
        if (cApplication != null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou're already writing a citizenship application to §f" + cApplication.getToCountryCountry().getName() + "§c!");
            ChatUtil.sendPrefixedMessage(sender, "§aCancelled the citizenship application!");
            return;
        }

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID());
        if (cApplications != null) {
            // if sent too many applications, escape
            int cApplicationsCount = cApplications.size();
            if (ConfigState.maxCitizenshipApplications != -1 && cApplicationsCount >= ConfigState.maxCitizenshipApplications) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou've already sent too many §f(" + cApplicationsCount + "/" + ConfigState.maxCitizenshipApplications + ")§c citizenship applications! Unsend one by doing §f/gc citizenship unapply [country]");
                return;
            }
            // if already sent application to this country, escape
            if (!ConfigState.debugMode && cApplications.stream().anyMatch(ca -> ca.getToCountry().equals(toCountry.getUUID()))) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou already have a pending citizenship application to §f" + countryName + "§c!");
                return;
            }
        }

        // create new application
        UUID playerUUID = playerProfile.getUUID();
        cApplication = new CitizenshipApplication(UUID.randomUUID(),
                                                  playerUUID,
                                                  toCountry.getUUID()); // reuse variable
        cApplication.setReason("N/A");
        CitizenshipApplicationService.open(cApplication, true);

        // if country has auto-accept enabled, accept and escape
        if (toCountry.getSettings().get("autoacceptcitizenshipapplications").equals("true")) {
            CitizenshipApplicationService.cancel(cApplication, false); // cancel open application
            CitizenshipApplicationService.accept(cApplication, true); // send sent application
            // broadcast notif to country
            ChatUtil.broadcastPrefixedMessageToCountry(toCountry, "§f" + playerProfile.getUsername() + "§6 is now a citizen of §f" + toCountry.getName() + "§6!", false);
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

        cApplication.setReason(responseClean);

        // send application
        CitizenshipApplicationService.send(cApplication, true);
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        List<Country> invalidCountries;
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(UuidUtil.getUUIDOfCommandSender(sender));
        // if sent no applications, no invalid countries
        if (cApplications == null || cApplications.isEmpty())
            invalidCountries = List.of();
        // else exclude countries which we applied to before
        else
            invalidCountries = cApplications.stream()
                                            .map(CitizenshipApplication::getToCountryCountry).toList();
        // negate
        List<Country> validCountries = Country.all.stream()
                                                  .filter(c -> !invalidCountries.contains(c)).toList();

        return MenuPage.createSkullMenuButtons(validCountries, country -> country.getLeaderObject().getOfflinePlayer(),
                                                               country -> "§a" + country.getName(),
                                                               country -> "Apply to §6" + country.getName(),
                                                               country -> "gc citizenship apply " + country.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsNames(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.hasCitizenship())
            return false;

        return CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID()) == null;
    }
}
