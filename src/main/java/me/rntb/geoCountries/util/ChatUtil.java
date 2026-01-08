package me.rntb.geoCountries.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class ChatUtil {

    // all
    public static void BroadcastPrefixedMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SendPrefixedMessage(player, message);
        }
    }

    // player
    public static void SendPrefixedMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GRAY + "GeoCountries: §r" + message + ChatColor.RESET);
    }

    public static void SendNoPermissionMessage(CommandSender sender, String command, String permission) {
        SendPrefixedMessage(sender, ChatColor.RED + "You do not have permission to run " + ChatColor.RESET + command + ChatColor.RED + "! " +
                                    ChatColor.DARK_GRAY + "(" + permission + ")" + ChatColor.RESET);
    }

    // console
    public static void SendPrefixedLogMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "GeoCountries: " + ChatColor.RESET + message);
    }

    public static void SendPrefixedLogErrorMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "GeoCountries: " + ChatColor.RED + message + ChatColor.RESET);
    }

    public static void SendPrefixedPlayerOnlyErrorMessage(String command) {
        ChatUtil.SendPrefixedLogErrorMessage("§cOnly players can run §f" + command + "§c!§r");
    }
}
