package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class gcCitizenshipUnsend extends GeoCommand {

    public gcCitizenshipUnsend(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Unsends a citizenship application that you previously sent.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6To what country was the citizenship application sent to which you want to unsend?");
            // start waiting for response
            Response.startWaiting(player.uuid,
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args);
            onResponse(sender, countryName);
        }

    }

    private void onResponse(CommandSender sender, String countryName) {
        PlayerProfile player = PlayerProfile.get(sender);
        Country toCountry = Country.get(countryName);

        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.uuid);

        // if doesnt have any pending applications, escape
        if (cApplications == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't got a pending application to §f" + countryName + "§c!");
            return;
        }

        CitizenshipApplication cApplication = cApplications.stream()
                                                           .filter(ca -> ca.toCountry.equals(toCountry.uuid))
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
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile player = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (player.position != Position.LEADER)
            return List.of();

        return player.getSentCitizenshipApplicationsAsNames();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile player = PlayerProfile.get(sender);
        if (player.hasCitizenship())
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.uuid);
        return cApplications != null && !cApplications.isEmpty();
    }
}
