package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcAdminDeleteCountry extends GeoCommand {

    public gcAdminDeleteCountry(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Deletes a country from the server.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the name of the country you want to delete!");
            return;
        }

        String countryName = String.join(" ", args).trim();

        Country country = Country.get(countryName);
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

    private void onConfirm(CommandSender sender, String[] args) {
        Country country = Country.get(args[0]);
        country.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aDeleted country!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }
}
