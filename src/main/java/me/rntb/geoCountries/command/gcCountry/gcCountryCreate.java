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

public class gcCountryCreate {

    public static void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // already in country
        if (pd.hasCountry()) {
            CountryData country = pd.getCountry();
            ChatUtil.SendPrefixedMessage(sender, "§cYou must first renounce your citizenship of §f" + country.Name + "§c using §f/gc citizenship renounce§c before creating a country!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to create!");
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
                                 Triple.of(gcCountryCreate::onConfirm,
                                         sender,
                                         new String[] { countryName })); // 0 = name
    }

    public static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());

        CountryData newCountry = new CountryData(countryName,
                UUID.randomUUID());
        newCountry.Leader = playerData.Uuid;
        newCountry.Citizens.add(playerData.Uuid);

        // create country
        CountryData.AddNew(newCountry);

        // set player country and rank
        playerData.setCountry(newCountry, PlayerData.PlayerRank.LEADER);

        ChatUtil.SendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.BroadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }
}
