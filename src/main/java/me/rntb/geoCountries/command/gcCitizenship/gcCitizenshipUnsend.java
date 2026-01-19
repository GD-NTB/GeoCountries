package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class gcCitizenshipUnsend {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you sent the citizenship application to!");
            return;
        }

        String countryName = String.join(" ", args);
        Country toCountry = Country.byName.get(countryName);

        // if country not exist, escape
        if (toCountry == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.uuid);

        // if doesnt have any pending applications, escape
        if (cApplications == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou have no pending citizenship applications!");
            return;
        }

        CitizenshipApplication cApplication = cApplications.stream().filter(ca -> ca.toCountry.equals(toCountry.uuid)).findFirst().orElse(null);

        // if havent sent application to this country, escape
        if (cApplication == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou haven't got a pending application to §f" + countryName + "§c!");
            return;
        }

        CitizenshipApplication.deleteSent(cApplication);

        ChatUtil.sendPrefixedMessage(sender, "§aUnsent citizenship application!");
    }
}
