package me.rntb.geoCountries.type;

import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;

import java.util.List;

// essentially a struct for the "metadata" of a setting
public class SettingData {

    private final String defaultValue;
    public String getDefaultValue() {
        return defaultValue;
    }

    public enum Type {
        BOOL,
        INT,
        STRING,
        COUNTRY_PREFIX,
        CHAT_COLOUR,
        COUNTRY_MOTTO,
        COLOUR
    }
    private final Type type;
    public Type getType() {
        return type;
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final String description;
    public String getDescription() {
        return description;
    }

    // value for numbers, length for strings
    private int min;
    public int getMin() {
        return min;
    }

    // value for numbers, length for strings
    private int max;
    public int getMax() {
        return max;
    }

    public SettingData(String defaultValue, Type type, String name, String description) {
        this.defaultValue = defaultValue;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public SettingData(String defaultValue, Type type, String name, String description, int min, int max) {
        this(defaultValue, type, name, description);
        this.min = min;
        this.max = max;
    }

    public String toString(String value) {
        return "§e%s: %s"
               .formatted(name,
                          getValueChatColour(value) + value);
    }

    public String toStringFull(String key, String value) {
        return "§e%s §8(%s)§f - %s: %s"
               .formatted(name,
                          key,
                          description,
                          getValueChatColour(value) + value);
    }

    public String getValueChatColour(String value) {
        return switch (type) {
            case Type.BOOL -> {
                if (value.equals("true"))
                    yield "§a";
                else if (value.equals("false"))
                    yield "§c";
                else
                    yield "§r";
            }
            case INT -> "§r";
            case STRING, COUNTRY_PREFIX, COUNTRY_MOTTO -> value.equals("null") ? "§c" : "§r";
            case CHAT_COLOUR -> {
                ChatUtil.ChatColour chatColour;
                try {
                    chatColour = ChatUtil.ChatColour.valueOf(value);
                } catch (IllegalArgumentException e) {
                    yield "§r";
                }
                yield ChatUtil.getChatColourByEnum(chatColour);
            }
            case COLOUR -> ChatUtil.getColouredString(value);
        };
    }

    public List<String> getTabCompletion() {
        return switch (type) {
            case BOOL -> List.of("true", "false");
            case STRING, COUNTRY_PREFIX, COUNTRY_MOTTO -> List.of("null");
            case CHAT_COLOUR -> EnumUtil.enumToStringList(ChatUtil.ChatColour.class);
            case INT -> List.of();
            case COLOUR -> List.of("#000000", "#ff0000", "#00ff00", "#0000ff", "#ffff00", "#ff00ff", "#00ffff", "#ffffff");
        };
    }
}
