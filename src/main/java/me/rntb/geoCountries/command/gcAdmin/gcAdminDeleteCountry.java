package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
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

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcAdminDeleteCountry::onConfirm,
                                          sender,
                                          new String[] { countryName }),
                                  true);
    }

    private static void onConfirm(CommandSender sender, String[] args) {
        Country country = Country.byName.get(args[0]);
        Country.delete(country);

        ChatUtil.sendPrefixedMessage(sender, "§aDeleted country!");
    }
}
