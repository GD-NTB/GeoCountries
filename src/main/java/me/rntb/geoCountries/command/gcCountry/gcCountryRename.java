package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.gcConfirm;
import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class gcCountryRename {

    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // if not in country, escape
        if (!pd.hasCountry()) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of a country to rename it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of your country to change its name!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the new name of the country!");
            return;
        }

        String countryName = String.join(" ", args);

        // validation check
        CountryData.NameValidation validation = CountryData.ValidateName(countryName, true);
        if (validation != CountryData.NameValidation.OK) {
            ChatUtil.SendPrefixedMessage(sender, CountryData.GetNameValidationString(validation));
            return;
        }

        // start waiting for confirm
        gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                 Triple.of(gcCountryRename::onConfirm,
                                         sender,
                                         new String[] { countryName })); // 0 = name
    }

    private static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());
        CountryData country = playerData.getCountry();

        ChatUtil.BroadcastPrefixedMessage("§6The country of §f" + country.Name + "§6 has been renamed to §f" + countryName + "§6!");

        country.setName(countryName);

        ChatUtil.SendPrefixedMessage(sender, "§aRenamed country to §f" + countryName + "§a!");
    }
}
