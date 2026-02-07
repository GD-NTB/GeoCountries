package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.SettingData;
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
            SettingData.setSetting(sender, args[0], toValue, playerProfile.settings);
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
            String key = args[0];
            TextComponent.Builder messageSpecificComponent = getMessageSpecific(key, playerProfile);
            if (messageSpecificComponent == null)
                message.append(Component.text("§cSetting §f" + key + "§c could not be found!"));
            else
                message.append(messageSpecificComponent);
            message.append(Component.newline());
        }

        message.append(Component.text("§6============================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static TextComponent.Builder getMessageAll(PlayerProfile playerProfile) {
        TextComponent.Builder message = Component.text();
        for (String key : playerProfile.settings.keySet()) {
            SettingData settingData = SettingData.map.get(key);
            if (settingData == null)
                continue;
            message.append(Component.text("§f> " + settingData.toString(playerProfile.settings.get(key)) + " "))
                   .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                               "/gc player settings " + key + " " + settingData.defaultValue))
                   .append(Component.newline());
        }
        return message;
    }

    private static TextComponent.Builder getMessageSpecific(String key, PlayerProfile playerProfile) {
        SettingData settingData = SettingData.map.get(key);
        if (settingData == null)
            return null;
        return Component.text().append(Component.text(settingData.toStringFull(key, playerProfile.settings.get(key)) + " "))
                               .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                                           "/gc player settings " + key + " " + settingData.defaultValue));
    }
}
