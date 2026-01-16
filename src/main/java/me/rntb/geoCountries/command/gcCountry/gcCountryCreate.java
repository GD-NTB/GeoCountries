package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class gcCountryCreate {

    public static void doCommand(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        PlayerProfile pd = PlayerProfile.byUUID.get(playerUUID);

        // already has citizenship
        if (pd.hasCitizenship()) {
            Country country = pd.getCitizenship();
            ChatUtil.sendPrefixedMessage(sender, "§cYou must first renounce your citizenship of §f" + country.name + "§c using §f/gc citizenship renounce§c before creating a country!");
            return;
        }

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

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcCountryCreate::onConfirm,
                                                   sender,
                                                   new String[] { countryName }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());

        Country newCountry = new Country(UUID.randomUUID(),
                                         countryName);
        newCountry.leader = playerProfile.uuid;
        newCountry.citizens.add(playerProfile.uuid);

        // create country
        Country.addNew(newCountry);

        // set player citizenship and rank
        playerProfile.setCitizenship(newCountry, PlayerProfile.PlayerRank.LEADER);

        ChatUtil.sendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
