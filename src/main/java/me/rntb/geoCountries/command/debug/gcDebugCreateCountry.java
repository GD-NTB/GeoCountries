package me.rntb.geoCountries.command.debug;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class gcDebugCreateCountry {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to create!");
            return;
        }

        String countryName = String.join(" ", args).trim();

        // validation check
        String validationString = StringUtil.ValidateCountryName(countryName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.leader = playerProfile.uuid;
        newCountry.citizens.add(playerProfile.uuid);

        // create country
        Country.addNew(newCountry);

        // set player citizenship and rank
        playerProfile.setCitizenship(newCountry, PlayerProfile.PlayerRank.LEADER);

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
