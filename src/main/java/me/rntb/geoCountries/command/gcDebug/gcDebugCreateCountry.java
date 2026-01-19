package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;

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

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.leader = null;

        // create country
        Country.addNew(newCountry);

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
