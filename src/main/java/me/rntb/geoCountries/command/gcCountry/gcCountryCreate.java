package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

// todo: use chat response when just doing /gc country create
public class gcCountryCreate extends GeoCommand {

    public gcCountryCreate(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Creates a new country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

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
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        Country newCountry = new Country(UUID.randomUUID(), countryName);
        newCountry.leader = playerProfile.uuid;
        newCountry.citizens.add(playerProfile.uuid);

        // create country
        Country.addNew(newCountry);

        // set player citizenship and rank
        playerProfile.setCitizenship(newCountry, PlayerProfile.PlayerRank.LEADER);

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");

        // play sound to creator
        SoundUtil.playSound((Player) sender, SoundUtil.SoundEffect.CHAT_NOTIF);
    }
}
