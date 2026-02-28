package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcDebugCreateCountry extends GeoCommand {

    public gcDebugCreateCountry(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Creates a test country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What do you want the name of the new country to be?");
            // start waiting for response
            Response.startWaiting(PlayerProfile.get(sender).uuid,
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args).trim();
            onResponse(sender, countryName);
        }
    }

    private void onResponse(CommandSender sender, String countryName) {
        // validation check
        String validationString = StringUtil.validateCountryName(countryName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.leader = null;

        // create country
        newCountry.register();

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
