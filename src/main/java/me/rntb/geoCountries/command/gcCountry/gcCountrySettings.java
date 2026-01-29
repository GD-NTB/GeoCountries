package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Setting;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class gcCountrySettings {

    public static void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the citizen of a country to see/change its settings!");
            return;
        }

        Country country = playerProfile.getCitizenship();

        // if setting a setting, set and escape
        if (args.length >= 2) {
            // if not leader, escape
            if (playerProfile.rank != PlayerProfile.PlayerRank.LEADER) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to change its settings!");
                return;
            }
            String toValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            setSetting(sender, args[0], toValue, country);
            return;
        }
        // else list all/specific setting
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== SETTINGS ==========\n");

        // if no args, list all settings
        if (args.length == 0)
            sb.append(getMessageAll(country)).append("\n");
        // else list specific setting
        else {
            String commandKey = args[0];
            String message = getMessageSpecific(commandKey, country);
            if (message == null)
                sb.append("§cSetting §f").append(commandKey).append("§c could not be found!");
            else
                sb.append(message).append("\n");
        }

        sb.append("§6==========================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }

    // todo: set to default option
    private static void setSetting(CommandSender sender, String command, String toValue, Country country) {
        Setting setting = country.getSetting(command);
        if (setting == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cSetting §f" + command + "§c could not be found!");
            return;
        }
        // validate and set value
        switch (setting.type) {
            case BOOL:
                if (toValue.equalsIgnoreCase("true"))
                    setting.value = "true";
                else if (toValue.equalsIgnoreCase("false"))
                    setting.value = "false";
                else {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (boolean) must either be §ftrue§c or §ffalse§c");
                    return;
                }
                break;

            case INT:
                int toValueInt;
                try {
                    toValueInt = Integer.parseInt(toValue);
                } catch (NumberFormatException e) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (integer) must be a whole number!");
                    return;
                }
                if (!(setting.intMinValue <= toValueInt && toValueInt <= setting.intMaxValue)) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe value for this setting (integer) must be between §f%d and %d§c!"
                                                         .formatted(setting.intMinValue, setting.intMaxValue));
                    return;
                }
                setting.value = toValue;
                break;

            case STRING:
                int toValueLength = toValue.length();
                if (!(setting.stringMinLength <= toValueLength && toValueLength <= setting.stringMaxLength)) {
                    ChatUtil.sendPrefixedMessage(sender, "§cThe length of the value for this setting (string) must be between §f%d and %d§c!"
                            .formatted(setting.stringMinLength, setting.stringMaxLength));
                    return;
                }
                break;

            default:
                setting.value = toValue;
                break;
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet §f" + command + "§a to §f" + toValue + "§a!");
    }

    // todo: paginate
    private static String getMessageAll(Country country) {
        StringBuilder message = new StringBuilder();
        // todo: buttons to type command to change setting in chat but not send
        for (Setting setting : country.settings) {
            message.append("§f> ").append(setting.toString()).append("\n");
        }
        return String.valueOf(message);
    }

    private static String getMessageSpecific(String command, Country country) {
        Setting setting = country.getSetting(command);
        if (setting == null)
            return null;
        return setting.toStringFull();
    }
}
