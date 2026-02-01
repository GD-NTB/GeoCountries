package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Setting;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class gcPlayerSettings {

    public static void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if setting a setting, set and escape
        if (args.length >= 2) {
            String toValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            setSetting(sender, args[0], toValue, playerProfile);
            return;
        }
        // else list all/specific setting
        // create and build component
        TextComponent.Builder message = Component.text();
        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== SETTINGS =========="))
               .append(Component.newline());

        // if no args, list all settings
        if (args.length == 0)
            message.append(getMessageAll(playerProfile));
        // else list specific setting
        else {
            String commandKey = args[0];
            TextComponent.Builder messageSpecificComponent = getMessageSpecific(commandKey, playerProfile);
            if (messageSpecificComponent == null)
                message.append(Component.text("§cSetting §f" + commandKey + "§c could not be found!"));
            else
                message.append(messageSpecificComponent);
            message.append(Component.newline());
        }

        message.append(Component.text("§6=========================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static void setSetting(CommandSender sender, String command, String toValue, PlayerProfile playerProfile) {
        Setting setting = playerProfile.getSetting(command);
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

        ChatUtil.sendPrefixedMessage(sender, "§aSet §e" + command + "§a to §f" + toValue + "§a!");
    }

    private static TextComponent.Builder getMessageAll(PlayerProfile playerProfile) {
        TextComponent.Builder message = Component.text();
        for (Setting setting : playerProfile.settings) {
            message.append(Component.text("§f> " + setting + " "))
                   .append(Setting.getEditButtonComponents("/gc player settings " + setting.key + " ",
                                                           "/gc player settings " + setting.key + " " + setting.defaultValue))
                   .append(Component.newline());
        }
        return message;
    }

    private static TextComponent.Builder getMessageSpecific(String command, PlayerProfile playerProfile) {
        Setting setting = playerProfile.getSetting(command);
        if (setting == null)
            return null;
        return Component.text().append(Component.text(setting.toStringFull() + " "))
                               .append(Setting.getEditButtonComponents("/gc player settings " + setting.key + " ",
                                                                       "/gc player settings " + setting.key + " " + setting.defaultValue));
    }
}
