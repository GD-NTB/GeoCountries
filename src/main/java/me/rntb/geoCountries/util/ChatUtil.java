package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

// todo: broadcast in country
public class ChatUtil {

    public static String newlineIfPrefixIsEmpty() {
        return ConfigState.ChatPrefix.length() <= 2 ? "" : "\n"; // empty = '', so length = 2
    }
    public static Component newlineIfPrefixIsEmptyComponent() {
        return ChatUtil.newlineIfPrefixIsEmpty().isEmpty() ? Component.empty() : Component.text(ChatUtil.newlineIfPrefixIsEmpty());
    }

    // all
    public static void broadcastPrefixedMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers())
            sendPrefixedMessage(player, message);
    }


    // player
    public static void sendPrefixedMessage(CommandSender sender, String message) {
        if (sender == null)
            return;
        sender.sendMessage(ConfigState.ChatPrefix + message);
    }
    public static void sendPrefixedMessage(CommandSender sender, Component message) {
        if (sender == null)
            return;
        sender.sendMessage(ConfigState.ChatPrefixComponent.append(message));
    }

    public static void sendNoPermissionMessage(CommandSender sender, String command, String permission) {
        sendPrefixedMessage(sender, "§cYou do not have permission to do §f" + command + "§c! §8(" + permission + ")");
    }

    // console
    public static void sendPrefixedLogMessage(String message) {
        getServer().getConsoleSender().sendMessage(ConfigState.ChatPrefix + message);
    }

    public static void sendPrefixedLogErrorMessage(String message) {
        getServer().getConsoleSender().sendMessage(ConfigState.ChatPrefix + "§c" + message);
    }

    public static void sendPrefixedPlayerOnlyErrorMessage(String command) {
        ChatUtil.sendPrefixedLogErrorMessage("§cOnly players can do §f" + command + "§c!");
    }
}
