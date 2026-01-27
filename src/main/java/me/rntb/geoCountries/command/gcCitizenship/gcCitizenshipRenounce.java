package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcCitizenshipRenounce {

    public static void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile pd = PlayerProfile.get(player);

        // if doesnt have citizenship, escape
        if (!pd.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be have citizenship of a country in order to renounce it!");
            return;
        }

        // if leader of country, escape
        if (pd.rank == PlayerProfile.PlayerRank.LEADER) { // todo: this will eventually be replaced by a system where there is a chosen leader inheritor
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't renounce your citizenship if you are the leader of the country, you must either promote another player to leader (§f/gc country promote [player]§c) or dissolve the country (§f/gc country dissolve§c)!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(gcCitizenshipRenounce::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private static void onConfirm(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);

        Country country = playerProfile.getCitizenship();
        ChatUtil.sendPrefixedMessage(sender, "§aRenounced your citizenship of §f" + country.name + "§a!");

        playerProfile.clearCitizenship();

        // play sound to renouncer
        SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);

        // broadcast notif to country (todo: settings)
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.username + "§6 is no longer a citizen of §f" + country.name + "§6!", true);
    }
}
