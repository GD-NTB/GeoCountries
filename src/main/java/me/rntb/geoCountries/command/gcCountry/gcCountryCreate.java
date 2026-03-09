package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.service.PositionService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class gcCountryCreate extends GeoCommand {

    public gcCountryCreate(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Creates a new country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        // already has citizenship
        if (player.hasCitizenship()) {
            Country country = player.getCitizenshipCountry();
            ChatUtil.sendPrefixedMessage(sender, "§cYou must first renounce your citizenship of §f" + country.getName() + "§c using §f/gc citizenship renounce§c before creating a country!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What do you want the name of your new country to be?");
            // start waiting for response
            Response.startWaiting(player.getUUID(),
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
        PlayerProfile player = PlayerProfile.get(sender);

        // validation check
        String validationString = StringUtil.validateCountryName(countryName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }


        Country newCountry = new Country(UUID.randomUUID(), countryName);

        // create country
        newCountry.register();

        // set player citizenship and position
        CitizenshipService.joinCountry(player, newCountry);
        PositionService.promoteToLeader(player);

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aCreated country §f" + countryName + "§a!");

        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return !PlayerProfile.get(sender).hasCitizenship();
    }
}
