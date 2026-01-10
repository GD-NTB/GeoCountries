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

public class gcCountryDissolve {

    public static void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // if not in country, escape
        if (!pd.hasCountry()) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        gcConfirm.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                 Triple.of(gcCountryDissolve::onConfirm,
                                         sender,
                                         new String[] { }));
    }

    private static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());
        CountryData country = playerData.getCountry();

        ChatUtil.SendPrefixedMessage(sender, "§aDissolved country §f" + country.Name + "§a!");
        ChatUtil.BroadcastPrefixedMessage("§6The country §f" + country.Name + "§6 has just been dissolved!");

        CountryData.Delete(country);
    }
}
