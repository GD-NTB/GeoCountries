package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcDebugSoundTest {

    public static void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendPrefixedPlayerOnlyErrorMessage("/gc debug soundtest");
            return;
        }

        SoundUtil.PlaySound(player, SoundUtil.SoundEffect.CHAT_NOTIF);

        ChatUtil.sendPrefixedMessage(sender, "§aDing!");
    }
}
