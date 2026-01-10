package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class gcPurgePlayerData {
    
    public static void onConfirm(@NotNull CommandSender sender, @NotNull String[] args) {
        int allLength = PlayerData.All.toArray().length;
        for (PlayerData pd : new ArrayList<>(PlayerData.All)) { // new ArrayList as we are concurrently modifying
            PlayerData.Delete(pd);
        }
        ChatUtil.SendPrefixedMessage(sender, "§aPurged §f" + allLength + "§a PlayerDatas.");
    }
}
