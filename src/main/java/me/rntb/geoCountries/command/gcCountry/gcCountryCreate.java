package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.service.RankService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

// todo: use chat response when just doing /gc country create
public class gcCountryCreate extends GeoCommand {

    public gcCountryCreate(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Creates a new country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // already has citizenship
        if (playerProfile.hasCitizenship()) {
            Country country = playerProfile.getCitizenship();
            ChatUtil.sendPrefixedMessage(sender, "§cYou must first renounce your citizenship of §f" + country.name + "§c using §f/gc citizenship renounce§c before creating a country!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the country you want to create!");
            return;
        }

        String countryName = String.join(" ", args).trim();

        // validation check
        String validationString = StringUtil.validateCountryName(countryName, true);
        if (validationString != null) { // validation.OK -> null
            ChatUtil.sendPrefixedMessage(sender, validationString);
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
        String countryName = args[0];
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.leader = playerProfile.uuid;
        newCountry.citizens.add(playerProfile.uuid);

        // create country
        newCountry.register();

        // set player citizenship and rank
        CitizenshipService.joinCountry(playerProfile, newCountry);
        RankService.promoteToLeader(playerProfile);

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aCreated country §f" + countryName + "§a!");

        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return !PlayerProfile.get(sender).hasCitizenship();
    }
}
