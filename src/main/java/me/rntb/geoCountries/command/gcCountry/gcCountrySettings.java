package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class gcCountrySettings extends GeoCommand {

    public gcCountrySettings(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Sets/lists your country's settings.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the citizen of a country to see/change its settings!");
            return;
        }

        Country country = playerProfile.getCitizenship();

        // if setting a setting, set and escape
        boolean isLeader = playerProfile.rank == PlayerProfile.PlayerRank.LEADER;
        if (args.length >= 2) {
            // if not leader, escape
            if (playerProfile.rank != PlayerProfile.PlayerRank.LEADER) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to change its settings!");
                return;
            }
            String toValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            SettingData.setSetting(sender, args[0], toValue, Country.settingsData, country.settings);
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
            message.append(getMessageAll(country, isLeader));
        // else list specific setting
        else {
            String commandKey = args[0];
            TextComponent.Builder messageSpecificComponent = getMessageSpecific(commandKey, country, isLeader);
            if (messageSpecificComponent == null)
                message.append(Component.text("§cSetting §f" + commandKey + "§c could not be found!"));
            else
                message.append(messageSpecificComponent);
            message.append(Component.newline());
        }

        message.append(Component.text("§6============================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private TextComponent.Builder getMessageAll(Country country, boolean isLeader) {
        TextComponent.Builder message = Component.text();
        for (String key : country.settings.keySet()) {
            SettingData settingData = Country.settingsData.get(key);
            if (settingData == null)
                continue;
            message.append(Component.text("§f> " + settingData.toString(country.settings.get(key)) + " "));
            if (isLeader)
                message.append(SettingData.getEditButtonComponents("/gc country settings " + key + " ",
                                                                   "/gc country settings " + key + " " + settingData.defaultValue));
            message.append(Component.newline());
        }
        return message;
    }

    private TextComponent.Builder getMessageSpecific(String key, Country country, boolean isLeader) {
        SettingData settingData = Country.settingsData.get(key);
        if (settingData == null)
            return null;
        TextComponent.Builder message = Component.text().append(Component.text(settingData.toStringFull(key, country.settings.get(key)) + " "));
        if (isLeader)
            message.append(SettingData.getEditButtonComponents("/gc country settings " + key + " ",
                                                               "/gc country settings " + key + " " + settingData.defaultValue));
        return message;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        Country playerCountry = Country.byCommandSender(sender);
        if (playerCountry == null)
            return List.of();

        // if no setting mentioned, return all settings as strings
        if (args.length == 1)
            return playerCountry.settings.keySet().stream().toList();

        // get setting typed before
        SettingData settingData = Country.settingsData.get(args[0]);
        if (settingData == null)
            return  List.of();
        // return possible values for this settings
        return settingData.getTabCompletion();
    }
}
