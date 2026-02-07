package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.SettingData;
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
            SettingData.setSetting(sender, args[0], toValue, country.settings);
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

        message.append(Component.text("§6============================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static TextComponent.Builder getMessageAll(Country country) {
        TextComponent.Builder message = Component.text();
        for (String key : country.settings.keySet()) {
            SettingData settingData = SettingData.map.get(key);
            if (settingData == null)
                continue;
            message.append(Component.text("§f> " + settingData.toString(country.settings.get(key)) + " "))
                   .append(SettingData.getEditButtonComponents("/gc country settings " + key + " ",
                                                               "/gc country settings " + key + " " + settingData.defaultValue))
                   .append(Component.newline());
        }
        return message;
    }

    private static TextComponent.Builder getMessageSpecific(String key, Country country) {
        SettingData settingData = SettingData.map.get(key);
        if (settingData == null)
            return null;
        return Component.text().append(Component.text(settingData.toStringFull(key, country.settings.get(key)) + " "))
                               .append(SettingData.getEditButtonComponents("/gc country settings " + key + " ",
                                                                           "/gc country settings " + key + " " + settingData.defaultValue));
    }
}
