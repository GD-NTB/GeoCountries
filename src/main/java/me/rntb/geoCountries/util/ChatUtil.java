package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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

    // country
    public static void broadcastPrefixedMessageToCountry(Country country, String message, boolean playSound) {
        for (UUID uuid : country.citizens) {
            PlayerProfile playerProfile = PlayerProfile.byUUID.get(uuid);
            if (ConfigState.DebugLogging && playerProfile == null) {
                sendPrefixedLogMessage("Tried to broadcast message to UUID " + uuid + " without PlayerProfile.");
                return;
            }
            Player player = playerProfile.getOnlinePlayer();

            sendPrefixedMessage(playerProfile.getOnlinePlayer(), message);

            // play sound
            if (playSound)
                SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
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
