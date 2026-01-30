package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcCountryDissolve {

    public static void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (playerProfile.getLeaderOf() == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(gcCountryDissolve::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
        Country country = playerProfile.getCitizenship();

        ChatUtil.sendPrefixedMessage(sender, "§aDissolved country §f" + country.name + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6The country §f" + country.name + "§6 has just been dissolved!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6Your country has just been dissolved! §cYou are no longer a citizen of any country.", true);

        Country.delete(country);
    }
}
