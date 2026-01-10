package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class gcPurgeUsername {

    public static void onConfirm(CommandSender sender, String[] args) {
        PlayerData playerData = PlayerData.PlayerDataByUsername.get(args[0]);
        PlayerData.Delete(playerData);

        ChatUtil.SendPrefixedMessage(sender, "§aPurged player §f\"" + playerData.Username + "\"§a.");
    }
}
