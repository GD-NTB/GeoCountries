package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcAdminDeleteCountry extends SubSubCommand {

    public gcAdminDeleteCountry(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
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
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                          sender,
                                          new String[] { countryName }),
                                  true);
    }

    @Override
    public void onConfirm(CommandSender sender, String[] args) {
        Country country = Country.byName.get(args[0]);
        Country.delete(country);

        ChatUtil.sendPrefixedMessage(sender, "§aDeleted country!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsNames(true) : List.of();
    }
}
