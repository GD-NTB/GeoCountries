package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.model.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class gcPlayerSettings extends GeoCommand {

    public gcPlayerSettings(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Sets/lists your settings.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if setting a setting, set and escape
        if (args.length >= 2) {
            String toValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            SettingData.setSetting(sender, args[0], toValue, PlayerProfile.settingsData, playerProfile.settings);
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

    private TextComponent.Builder getMessageAll(PlayerProfile playerProfile) {
        TextComponent.Builder message = Component.text();
        for (String key : playerProfile.settings.keySet()) {
            SettingData settingData = PlayerProfile.settingsData.get(key);
            if (settingData == null)
                continue;
            message.append(Component.text("§f> " + settingData.toString(playerProfile.settings.get(key)) + " "))
                   .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                               "/gc player settings " + key + " " + settingData.defaultValue))
                   .append(Component.newline());
        }
        return message;
    }

    private TextComponent.Builder getMessageSpecific(String key, PlayerProfile playerProfile) {
        SettingData settingData = PlayerProfile.settingsData.get(key);
        if (settingData == null)
            return null;
        return Component.text().append(Component.text(settingData.toStringFull(key, playerProfile.settings.get(key)) + " "))
                               .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                                           "/gc player settings " + key + " " + settingData.defaultValue));
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
        if (playerProfile == null)
            return List.of();

        // if no setting mentioned, return all settings as strings
        if (args.length == 1)
            return playerProfile.settings.keySet().stream().toList();

        // get setting typed before
        SettingData settingData = PlayerProfile.settingsData.get(args[0]);
        if (settingData == null)
            return List.of();
        // return possible values for this setting
        return settingData.getTabCompletion();
    }
}
