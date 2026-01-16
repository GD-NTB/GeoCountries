package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class gcCountryRename {

    public static void onCommand(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        PlayerProfile pd = PlayerProfile.byUUID.get(playerUUID);

        // if doesnt have citizenship, escape
        if (!pd.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to rename it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of your country to change its name!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new name of the country!");
            return;
        }

        String countryName = String.join(" ", args).trim();

        // validation check
        String validationString = StringUtil.ValidateCountryName(countryName, true);
        if (validationString != null) {
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcCountryRename::onConfirm,
                                                   sender,
                                                   new String[] { countryName }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());
        Country country = playerProfile.getCitizenship();

        ChatUtil.broadcastPrefixedMessage("§6The country of §f" + country.name + "§6 has been renamed to §f" + countryName + "§6!");

        country.setName(countryName);

        ChatUtil.sendPrefixedMessage(sender, "§aRenamed country to §f" + countryName + "§a!");
    }
}
