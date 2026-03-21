package me.rntb.geoCountries.type;

import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

// essentially a struct for the "metadata" of a setting
public class SettingData {

    public String defaultValue;
    public enum Type {
        BOOL,
        INT,
        STRING,
        COUNTRY_PREFIX,
        CHAT_COLOUR,
        COUNTRY_MOTTO,
        COLOUR
    }
    public Type type;
    public String name;
    public String description;

    // value for numbers, length for strings
    public int min;
    public int max;

    // todo: create service
    public static void setSetting(CommandSender sender, String key, String toValue, Map<String, SettingData> settingsData, Map<String, String> settingsRef) {
        SettingData settingData = settingsData.get(key);
        // if in settingsDataMapRef, should be in settingsMapRef, if not it's gonna be .put anyway (?!)
        if (settingData == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cSetting §f" + key + "§c could not be found!");
            return;
        }

        String toValueTrimmed = toValue.trim();
        String validationString; // java stop being homosexual

        // validate value
        switch (settingData.type) {
            case BOOL:
                if (!toValueTrimmed.equals("true") && !toValueTrimmed.equals("false")) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (boolean) must either be §ftrue§c or §ffalse§c!");
                    return;
                }
                break;

            case INT:
                int toValueInt;
                try {
                    toValueInt = Integer.parseInt(toValueTrimmed);
                } catch (NumberFormatException e) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (integer) must be a whole number!");
                    return;
                }
                if (!(settingData.min <= toValueInt && toValueInt <= settingData.max)) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (integer) must be between §f%d and %d§c!"
                                                         .formatted(settingData.min, settingData.max));
                    return;
                }
                break;

            case STRING:
                int toValueLength = toValueTrimmed.length();
                if (!(settingData.min <= toValueLength && toValueLength <= settingData.max)) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe length of the value for this setting (string) must be between §f%d and %d§c!"
                                                         .formatted(settingData.min, settingData.max));
                    return;
                }
                break;

            case COUNTRY_PREFIX:
                validationString = StringUtil.validateCountryPrefix(toValueTrimmed);
                if (validationString != null) {
                    ChatUtil.sendPrefixedMessage(sender, validationString);
                    return;
                }
                break;

            case CHAT_COLOUR:
                if (!EnumUtil.enumToStringList(ChatUtil.ChatColour.class).contains(toValueTrimmed)) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (chat colour) must be one of the available chat colours (look at the autocomplete options)!");
                    return;
                }
                break;

            case COUNTRY_MOTTO:
                validationString = StringUtil.validateCountryMotto(toValueTrimmed);
                if (validationString != null) {
                    ChatUtil.sendPrefixedMessage(sender, validationString);
                    return;
                }
                break;

            case COLOUR:
                if (!toValueTrimmed.startsWith("#")) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe hex code for this setting (colour) must start with a hash (#)!");
                    return;
                }
                String valueHexPart = toValueTrimmed.replace("#", "");
                if (valueHexPart.length() != 6) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (colour) must be a valid colour hex code!");
                    return;
                }
                try {
                    Integer.parseInt(valueHexPart, 16);
                } catch (NumberFormatException e) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (colour) must be a hex code!");
                    return;
                }
                break;
        }

        settingsRef.put(key, toValueTrimmed);

        ChatUtil.sendPrefixedMessage(sender, "§aSet §e" + key + "§a to §f" + toValueTrimmed + "§a!");
    }

    public SettingData(String defaultValue, Type type, String name, String description) {
        this.defaultValue = defaultValue;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public SettingData(String defaultValue, Type type, String name, String description, int min, int max) {
        this(defaultValue, type, name, description);
        this.min = min;
        this.max = max;
    }

    public String toString(String value) {
        return "§e%s: %s"
               .formatted(name,
                          getValueChatColour(value, type) + value);
    }

    public String toStringFull(String key, String value) {
        return "§e%s §8(%s)§f - %s: %s"
               .formatted(name,
                          key,
                          description,
                          getValueChatColour(value, type) + value);
    }

    public String getValueChatColour(String value, Type type) {
        return switch (type) {
            case Type.BOOL -> {
                if (value.equals("true"))
                    yield "§a";
                else if (value.equals("false"))
                    yield "§c";
                else
                    yield "§r";
            }
            case INT -> "§r";
            case STRING, COUNTRY_PREFIX, COUNTRY_MOTTO -> value.equals("null") ? "§c" : "§r";
            case CHAT_COLOUR -> {
                ChatUtil.ChatColour chatColour;
                try {
                    chatColour = ChatUtil.ChatColour.valueOf(value);
                } catch (IllegalArgumentException e) {
                    yield "§r";
                }
                yield ChatUtil.getChatColourByEnum(chatColour);
            }
            case COLOUR -> ChatUtil.getColouredString("", Integer.parseInt(value.replace("#", ""), 16));
        };
    }

    public List<String> getTabCompletion() {
        return switch (this.type) {
            case BOOL -> List.of("true", "false");
            case STRING, COUNTRY_PREFIX, COUNTRY_MOTTO -> List.of("null");
            case CHAT_COLOUR -> EnumUtil.enumToStringList(ChatUtil.ChatColour.class);
            case INT -> List.of();
            case COLOUR -> List.of("#000000", "#ff0000", "#00ff00", "#0000ff", "#ffff00", "#ff00ff", "#00ffff", "#ffffff");
        };
    }

    public static TextComponent.Builder getEditButtonComponents(String editCommand, String defaultCommand) {
        return Component.text()
                        // [Edit] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'" + editCommand + "'>" +
                                "<hover:show_text:\"<white>Click to edit the setting's value.</white>\">" +
                                "<dark_gray><bold>[Edit]</bold></dark_gray>" +
                                "</hover></click>"
                        ))
                        .append(Component.text(" "))
                        // [Default] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'" + defaultCommand + "'>" +
                                "<hover:show_text:\"<white>Click to set to default value.</white>\">" +
                                "<dark_gray><bold>[Default]</bold></dark_gray>" +
                                "</hover></click>"
                        ));
    }
}
