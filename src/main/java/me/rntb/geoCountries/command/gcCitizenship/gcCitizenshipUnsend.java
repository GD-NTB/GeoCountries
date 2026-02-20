package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class gcCitizenshipUnsend extends SubSubCommand {

    public gcCitizenshipUnsend(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
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

        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        ArrayList<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.uuid);

        // if doesnt have any pending applications, escape
        if (cApplications == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou have no pending citizenship applications!");
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

        cApplication.deleteSent();

        ChatUtil.sendPrefixedMessage(sender, "§aUnsent citizenship application!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (player.rank != PlayerProfile.PlayerRank.LEADER)
            return List.of();
        return player.getCitizenship().citizens.stream()
                                               .map(c -> PlayerProfile.byUUID.get(c).username).toList();
    }
}
