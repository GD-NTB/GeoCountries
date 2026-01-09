package me.rntb.geoCountries.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class ChatUtil {

    public static String ChatPrefix = ""; // set by config
    public static String NewlineIfPrefixIsEmpty() { return ChatPrefix.length() <= 2 ? "" : "\n"; } // empty = '', so length = 2

    // all
    public static void BroadcastPrefixedMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SendPrefixedMessage(player, message);
        }
    }

    // player
    public static void SendPrefixedMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatPrefix + message);
    }

    public static void SendNoPermissionMessage(CommandSender sender, String command, String permission) {
        SendPrefixedMessage(sender, "§cYou do not have permission to run §f" + command + "§c! §8(" + permission + ")");
    }

    // console
    public static void SendPrefixedLogMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatPrefix + message);
    }

    public static void SendPrefixedLogErrorMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatPrefix + "§c" + message);
    }

    public static void SendPrefixedPlayerOnlyErrorMessage(String command) {
        ChatUtil.SendPrefixedLogErrorMessage("§cOnly players can run §f" + command + "§c!");
    }
}
