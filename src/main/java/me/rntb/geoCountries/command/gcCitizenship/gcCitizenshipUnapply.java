package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class gcCitizenshipUnapply extends GeoCommand {

    public gcCitizenshipUnapply(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Unsends a citizenship application that you previously sent.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you sent the citizenship application to!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(sender);

        String countryName = String.join(" ", args);
        Country toCountry = Country.get(countryName);

        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID());

        // if doesnt have any pending applications, escape
        if (cApplications == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't got a pending application to §f" + countryName + "§c!");
            return;
        }

        CitizenshipApplication cApplication = cApplications.stream()
                                                           .filter(ca -> ca.getToCountry().equals(toCountry.getUUID()))
                                                           .findFirst().orElse(null);

        // if havent sent application to this country, escape
        if (cApplication == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't got a pending application to §f" + countryName + "§c!");
            return;
        }

        CitizenshipApplicationService.deleteSent(cApplication);

        ChatUtil.sendPrefixedMessage(sender, "§aUnsent citizenship application!");
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(UuidUtil.getUUIDOfCommandSender(sender));

        List<Country> validCountries = cApplications.stream()
                                                    .map(CitizenshipApplication::getToCountryCountry).toList();

        return MenuPage.createSkullMenuButtons(validCountries, country -> country.getLeaderObject().getOfflinePlayer(),
                                                               country -> "§a" + country.getName(),
                                                               country -> "Unapply from §6" + country.getName(),
                                                               country -> "gc citizenship unapply " + country.getName());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (playerProfile.getPosition() != Position.LEADER)
            return List.of();

        return playerProfile.getSentCitizenshipApplicationsAsNames();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.hasCitizenship())
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID());
        return cApplications != null && !cApplications.isEmpty();
    }
}
