package me.rntb.geoCountries.service;

import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import me.rntb.geoCountries.util.ValidationUtil;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class SettingService {

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
        switch (settingData.getType()) {
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
                if (!(settingData.getMin() <= toValueInt && toValueInt <= settingData.getMax())) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (integer) must be between §f%d and %d§c!"
                                                         .formatted(settingData.getMin(), settingData.getMax()));
                    return;
                }
                break;

            case STRING:
                int toValueLength = toValueTrimmed.length();
                if (!(settingData.getMin() <= toValueLength && toValueLength <= settingData.getMax())) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe length of the value for this setting (string) must be between §f%d and %d§c!"
                                                         .formatted(settingData.getMin(), settingData.getMax()));
                    return;
                }
                break;

            case COUNTRY_PREFIX:
                validationString = ValidationUtil.validateCountryPrefix(toValueTrimmed);
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
                validationString = ValidationUtil.validateCountryMotto(toValueTrimmed);
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

        String colourFormatter = settingData.getValueChatColour(toValueTrimmed);
        ChatUtil.sendPrefixedMessage(sender, "§aSet §e" + key + "§a to " + colourFormatter + toValueTrimmed + "§a!");
    }
}
