package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class gcPlayerSettings extends GeoCommand {

    public gcPlayerSettings(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists/manages your settings.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        // if setting a setting, set and escape
        if (args.length >= 2) {
            String toValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            SettingData.setSetting(sender, args[0], toValue, PlayerProfile.settingsData, player.settings);
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
            message.append(getMessageAll(player));
        // else list specific setting
        else {
            String key = args[0];
            TextComponent.Builder messageSpecificComponent = getMessageSpecific(key, player);
            if (messageSpecificComponent == null)
                message.append(Component.text("§cSetting §f" + key + "§c could not be found!"));
            else
                message.append(messageSpecificComponent);
            message.append(Component.newline());
        }

        message.append(Component.text("§6============================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private TextComponent.Builder getMessageAll(PlayerProfile player) {
        TextComponent.Builder message = Component.text();
        for (String key : player.settings.keySet()) {
            SettingData settingData = PlayerProfile.settingsData.get(key);
            if (settingData == null)
                continue;
            message.append(Component.text("§f> " + settingData.toString(player.settings.get(key)) + "  "))
                   .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                               "/gc player settings " + key + " " + settingData.defaultValue))
                   .append(Component.newline());
        }
        return message;
    }

    private TextComponent.Builder getMessageSpecific(String key, PlayerProfile player) {
        SettingData settingData = PlayerProfile.settingsData.get(key);
        if (settingData == null)
            return null;
        return Component.text().append(Component.text(settingData.toStringFull(key, player.settings.get(key)) + "  "))
                               .append(SettingData.getEditButtonComponents("/gc player settings " + key + " ",
                                                                           "/gc player settings " + key + " " + settingData.defaultValue));
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length > 2)
            return List.of();

        PlayerProfile player = PlayerProfile.get(sender);
        if (player == null)
            return List.of();

        // if no setting mentioned, return all settings as strings
        if (args.length == 1)
            return player.settings.keySet().stream().toList();

        // get setting typed before
        SettingData settingData = PlayerProfile.settingsData.get(args[0]);
        if (settingData == null)
            return List.of();
        // return possible values for this setting
        return settingData.getTabCompletion();
    }
}
