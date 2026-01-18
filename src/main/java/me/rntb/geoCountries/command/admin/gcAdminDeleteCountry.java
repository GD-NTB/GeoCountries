package me.rntb.geoCountries.command.admin;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcAdminDeleteCountry {

    public static void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to delete!");
            return;
        }

        String countryName = String.join(" ", args).trim();
        Country country = Country.byName.get(countryName);
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
            return;
        }

        Country.delete(country);

        ChatUtil.sendPrefixedMessage(sender, "§aDeleted country!");
    }
}
