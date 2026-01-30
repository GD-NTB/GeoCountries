package me.rntb.geoCountries.types;

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

    public void setToDefault() {
        this.value = this.defaultValue;
    }

    // gson sets transient fields to null so we gotta write them after gson writes key and value
    // definitely not the best way to do this at all, but it works...
    public void loadMetadata() {
        switch (this.key) {
            case "autoacceptcitizenshipapplications":
                this.defaultValue = "false";
                this.type = Setting.Type.BOOL;
                this.name = "Auto-Accept Citizenship Applications";
                this.description = "Automatically accept citizenship applications when received.";
                break;
        }
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
        return "§a%s §8(%s)§f: %s"
               .formatted(this.name,
                          this.key,
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
        return """
                §a%s
                §8(%s)§f: %s
                §e%s
                """
                .formatted(this.name,
                           this.key,
                           valueColour+this.value,
                           this.description);
    }
}
