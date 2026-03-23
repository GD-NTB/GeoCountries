package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ChatUtil {

    public static final MiniMessage mm = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer legacySerialisation = LegacyComponentSerializer.legacySection();

    public enum ChatColour {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_MAGENTA,
        GOLD,
        LIGHT_GREY,
        DARK_GREY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        MAGENTA,
        YELLOW,
        WHITE,
    }
    public static String getChatColourByEnum(ChatColour chatColour) {
        return "§" + switch (chatColour) {
            case BLACK -> "0";
            case DARK_BLUE -> "1";
            case DARK_GREEN -> "2";
            case DARK_AQUA -> "3";
            case DARK_RED -> "4";
            case DARK_MAGENTA -> "5";
            case GOLD -> "6";
            case LIGHT_GREY -> "7";
            case DARK_GREY -> "8";
            case BLUE -> "9";
            case GREEN -> "a";
            case AQUA -> "b";
            case RED -> "c";
            case MAGENTA -> "d";
            case YELLOW -> "e";
            case WHITE -> "f";
        };
    }

    public static String getColouredString(String string) {
        StringBuilder formatter = new StringBuilder("§x");
        for(char c : string.replace("#", "").toCharArray()) {
            formatter.append('§').append(c);
        }
        return formatter.toString();
    }

    // not centred automatically, you will need to prepend whitespace before appending these buttons
    public static TextComponent.Builder chatPageControlButtons(String commandForPrevious, String commandForNext, int effectiveIndex, int pageCount) {
        TextComponent.Builder message = Component.text();

        if (effectiveIndex > 1) {
            // [<<<] button
            message.append(mm.deserialize("<click:run_command:'" + commandForPrevious + "'>" +
                                          "<hover:show_text:'<white>Click to go to previous page.</white>'>" +
                                          "<dark_gray><bold>[<<<]</bold></dark_gray>" +
                                          "</hover></click>"))
                   .append(Component.text("  "));
        }
        else {
            message.append(Component.text("         "));
        }
        // (page/pages) text
        message.append(Component.text("§8(%d/%d)"
                                      .formatted(effectiveIndex, pageCount)))
               .append(Component.text("  "));
        if (effectiveIndex < pageCount) {
            // [>>>] button
            message.append(mm.deserialize("<click:run_command:'" + commandForNext + "'>" +
                                          "<hover:show_text:'<white>Click to go to next page.</white>'>" +
                                          "<dark_gray><bold>[>>>]</bold></dark_gray>" +
                                          "</hover></click>"));
        }

        return message;
    }

    public static String newlineIfPrefixIsEmpty() {
        return ConfigState.chatPrefix.length() <= 2 ? "" : "\n"; // empty = '', so length = 2
    }
    public static Component newlineIfPrefixIsEmptyComponent() {
        return ChatUtil.newlineIfPrefixIsEmpty().isEmpty() ? Component.empty() : Component.text(ChatUtil.newlineIfPrefixIsEmpty());
    }

    // all
    public static void broadcastPrefixedMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPrefixedMessage(player, message);
        }
    }
    public static void broadcastPrefixedMessage(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPrefixedMessage(player, message);
        }
    }

    // faction
    public static void broadcastPrefixedMessageToFaction(Faction faction, String message, boolean playSound) {
        for (UUID countryUUID : faction.getMembers()) {
            broadcastPrefixedMessageToCountry(Country.get(countryUUID), message, playSound);
        }
    }
    public static void broadcastPrefixedMessageToFaction(Faction faction, Component message, boolean playSound) {
        for (UUID countryUUID : faction.getMembers()) {
            broadcastPrefixedMessageToCountry(Country.get(countryUUID), message, playSound);
        }
    }

    // country
    public static void broadcastPrefixedMessageToCountry(Country country, String message, boolean playSound) {
        for (Player player : country.getOnlineCitizens()) {
            sendPrefixedMessage(player, message);
            if (playSound)
                SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }
    public static void broadcastPrefixedMessageToCountry(Country country, Component message, boolean playSound) {
        for (Player player : country.getOnlineCitizens()) {
            sendPrefixedMessage(player, message);
            if (playSound)
                SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
        }
    }

    // player
    public static void sendPrefixedMessage(CommandSender sender, String message) {
        if (sender == null)
            return;
        sender.sendMessage(ConfigState.chatPrefix + message);
    }
    public static void sendPrefixedMessage(CommandSender sender, Component message) {
        if (sender == null)
            return;
        sender.sendMessage(ConfigState.chatPrefixComponent.append(message));
    }

    public static void sendNoPermissionMessage(CommandSender sender, String command, String permission) {
        sendPrefixedMessage(sender, "§cYou do not have permission to do §f" + command + "§c! §8(" + permission + ")");
    }

    public static void sendPrefixedNotificationMessage(CommandSender sender, String message) {
        if (!(sender instanceof Player player))
            return;
        sendPrefixedMessage(sender, message);
        SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
    }
    public static void sendPrefixedNotificationMessage(CommandSender sender, Component message) {
        if (!(sender instanceof Player player))
            return;
        sendPrefixedMessage(sender, message);
        SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);
    }

    // console
    public static void sendPrefixedLogMessage(String message) {
        getServer().getConsoleSender().sendMessage(ConfigState.chatPrefix + "§r" + message);
    }

    public static void sendPrefixedLogErrorMessage(String message) {
        getServer().getConsoleSender().sendMessage(ConfigState.chatPrefix + "§c" + message);
    }
}
