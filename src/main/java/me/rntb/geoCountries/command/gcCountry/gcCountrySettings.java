package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Setting;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

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
        // create and build component
        TextComponent.Builder message = Component.text();
        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== SETTINGS =========="))
               .append(Component.newline());

        // if no args, list all settings
        if (args.length == 0)
            message.append(getMessageAll(country));
        // else list specific setting
        else {
            String commandKey = args[0];
            TextComponent.Builder messageSpecificComponent = getMessageSpecific(commandKey, country);
            if (messageSpecificComponent == null)
                message.append(Component.text("§cSetting §f" + commandKey + "§c could not be found!"));
            else
                message.append(messageSpecificComponent);
            message.append(Component.newline());
        }

        message.append(Component.text("§6=========================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

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
    private static TextComponent.Builder getMessageAll(Country country) {
        TextComponent.Builder message = Component.text();
        for (Setting setting : country.settings) {
            message.append(Component.text("§f> " + setting + " "))
                   .append(getEditButtonComponents(setting))
                   .append(Component.newline());
        }
        return message;
    }

    private static TextComponent.Builder getMessageSpecific(String command, Country country) {
        Setting setting = country.getSetting(command);
        if (setting == null)
            return null;
        return Component.text().append(Component.text(setting.toStringFull() + " "))
                               .append(getEditButtonComponents(setting));
    }

    private static TextComponent.Builder getEditButtonComponents(Setting setting) {
        return Component.text()
                        // [Edit] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'/gc country settings " + setting.key + " '>" +
                                "<hover:show_text:\"<dark_gray>Click to edit the setting's value.</dark_gray>\">" +
                                "<dark_gray><bold>[Edit]</bold></dark_gray>" +
                                "</hover></click>"
                        ))
                        .append(Component.text(" "))
                        // [Default] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'/gc country settings " + setting.key + " " + setting.defaultValue + "'>" +
                                "<hover:show_text:\"<dark_gray>Click to set to default value.</dark_gray>\">" +
                                "<dark_gray><bold>[Default]</bold></dark_gray>" +
                                "</hover></click>"
                        ));
    }
}
