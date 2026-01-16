package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class gcCountryDissolve {

    public static void onCommand(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        PlayerProfile pd = PlayerProfile.byUUID.get(playerUUID);

        // if doesnt have citizenship, escape
        if (!pd.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.GetUUIDOfCommandSender(sender),
                                  new Confirmation(gcCountryDissolve::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());
        Country country = playerProfile.getCitizenship();

        ChatUtil.sendPrefixedMessage(sender, "§aDissolved country §f" + country.name + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6The country §f" + country.name + "§6 has just been dissolved!");

        Country.delete(country);
    }
}
