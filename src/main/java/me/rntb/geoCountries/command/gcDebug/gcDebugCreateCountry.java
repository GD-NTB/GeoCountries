package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.ValidationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcDebugCreateCountry extends GeoCommand {

    public gcDebugCreateCountry(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Creates a test country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String countryName = String.join(" ", args).trim();

        // validation check
        String validationString = ValidationUtil.validateCountryName(countryName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.setLeaderInternal(null);

        // create country
        newCountry.register();

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
