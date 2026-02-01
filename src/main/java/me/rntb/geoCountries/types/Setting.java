package me.rntb.geoCountries.types;

import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;

public class Setting {

    public String key;
    public String value;

    public transient String defaultValue;
    public enum Type {
        BOOL,
        INT,
        STRING
    }
    public transient Type type;
    public transient String name;
    public transient String description;

    public transient int intMinValue;
    public transient int intMaxValue;

    public transient int stringMinLength;
    public transient int stringMaxLength;

    // gson sets transient fields to null so we gotta set them after gson writes key and value
    // definitely not the best way to do this at all, but it works...
    public void loadMetadata() {
        switch (this.key) {
            // country
            case "autoacceptcitizenshipapplications":
                this.defaultValue = "false";
                this.type = Setting.Type.BOOL;
                this.name = "Auto-Accept Citizenship Applications";
                this.description = "Automatically accept citizenship applications when received";
                break;
            // player
            case "chatnotificationsounds":
                this.defaultValue = "true";
                this.type = Setting.Type.BOOL;
                this.name = "Chat Notification Sounds";
                this.description = "Play a ding sound effect when receiving important chat messages.";
                break;
        }
    }

    public static Setting[] loadMetadataAndPurgeBroken(Setting[] settings) {
        ArrayList<Setting> newSettings = new ArrayList<>();
        ChatUtil.sendPrefixedLogMessage(Arrays.toString(settings));
        for (Setting setting : settings) {
            // if null, setting is broken, so purge
            if (setting == null) {
                continue;
            }
            setting.loadMetadata();
            // if name is null, setting is broken, so purge
            if (setting.name == null)
                continue;

            newSettings.add(setting);
        }
        // convert list to array
        Setting[] newSettingsArray = new Setting[newSettings.size()];
        return newSettings.toArray(newSettingsArray);
    }

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        String valueColour = "§f";
        if (this.type == Setting.Type.BOOL) {
            if (this.value.equals("true"))
                valueColour = "§a";
            else if (this.value.equals("false"))
                valueColour = "§c";
        }
        return "§e%s: %s"
               .formatted(this.name,
                          valueColour+this.value);
    }

    public String toStringFull() {
        String valueColour = "§f";
        if (this.type == Setting.Type.BOOL) {
            if (this.value.equals("true"))
                valueColour = "§a";
            else if (this.value.equals("false"))
                valueColour = "§c";
        }
        return "§e%s§f - %s: %s"
               .formatted(this.name,
                          this.description,
                          valueColour+this.value);
    }

    public static TextComponent.Builder getEditButtonComponents(String editCommand, String defaultCommand) {
        return Component.text()
                        // [Edit] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'" + editCommand + "'>" +
                                "<hover:show_text:\"<dark_gray>Click to edit the setting's value.</dark_gray>\">" +
                                "<dark_gray><bold>[Edit]</bold></dark_gray>" +
                                "</hover></click>"
                        ))
                        .append(Component.text(" "))
                        // [Default] button
                        .append(ChatUtil.mm.deserialize(
                                "<click:suggest_command:'" + defaultCommand + "'>" +
                                "<hover:show_text:\"<dark_gray>Click to set to default value.</dark_gray>\">" +
                                "<dark_gray><bold>[Default]</bold></dark_gray>" +
                                "</hover></click>"
                        ));
        //
        //
    }
}
