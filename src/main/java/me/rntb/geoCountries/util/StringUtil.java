package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String leadingS(long count) {
        return count == 1 ? "" : "s";
    }

    public static String sentenceCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String appendTrailingResetFormatter(String s) {
        return s != null && s.endsWith("§r") ? s : s + "§r";
    }

    public static String stripTrailingResetFormatter(String s) {
        return s != null && s.endsWith("§r") ? s.substring(0, s.length() - 2) : s;
    }

    // ---------- string validation ----------

    // ----- response -----
    // response should be trimmed beforehand
    public static String validateResponse(String response) {
        if (!(ConfigState.chatResponseMin <= response.length() && response.length() <= ConfigState.chatResponseMax))
            return "§cChat message must be between §f%d and %d§c characters!§r"
                    .formatted(ConfigState.chatResponseMin, ConfigState.chatResponseMax);

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}\\p{N} ,.?!;:£$%^&*'()=+_#\\[\\]/\\-]").matcher(response);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following character(s) are not allowed: §r" + String.join("", illegalChars) + "§r";
        if (response.chars().anyMatch(ch -> ch < 32))
            return "§cChat message must not contain any control characters!";

        // response is valid
        return null;
    }

    // ----- country name -----
    // country name should be trimmed beforehand
    public static String validateCountryName(String countryName, boolean alreadyExistsInvalid) {
        if (!(ConfigState.countryNameMin <= countryName.length() && countryName.length() <= ConfigState.countryNameMax))
            return "§cCountry name must be between §f%d and %d§c characters!§r"
                   .formatted(ConfigState.countryNameMin, ConfigState.countryNameMax);
        if (alreadyExistsInvalid && Country.get(countryName) != null)
            return "§cA country with that name already exists!§r";

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}0-9', ()./_-]").matcher(countryName);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following characters are not allowed in a country name: §r" + String.join("", illegalChars) + "§r";
        if (countryName.chars().anyMatch(ch -> ch < 32))
            return "§cCountry name must not contain any control characters!";

        // country name is valid
        return null;
    }

    // ----- country prefix -----
    // country prefix should be trimmed beforehand
    public static String validateCountryPrefix(String prefix) {
        if (!(ConfigState.countryPrefixMin <= prefix.length() && prefix.length() <= ConfigState.countryPrefixMax))
            return "§cCountry prefix must be between §f%d and %d§c characters!§r"
                   .formatted(ConfigState.countryPrefixMin, ConfigState.countryPrefixMax);

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}0-9',()./_-]").matcher(prefix);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following character(s) are not allowed in a country prefix: §r" + String.join("", illegalChars) + "§r";
        if (prefix.chars().anyMatch(ch -> ch < 32))
            return "§cCountry prefix must not contain any control characters!";

        // country prefix is valid
        return null;
    }

    // ----- country motto -----
    // country motto should be trimmed beforehand
    public static String validateCountryMotto(String motto) {
        if (!(ConfigState.countryMottoMin <= motto.length() && motto.length() <= ConfigState.countryMottoMax))
            return "§cCountry motto must be between §f%d and %d§c characters!§r"
                    .formatted(ConfigState.countryMottoMin, ConfigState.countryMottoMax);

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}0-9', ()./_-]").matcher(motto);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following character(s) are not allowed in a country motto: §r" + String.join("", illegalChars) + "§r";
        if (motto.chars().anyMatch(ch -> ch < 32))
            return "§cCountry motto must not contain any control characters!";

        // country prefix is valid
        return null;
    }

    // ----- faction name -----
    // faction name should be trimmed beforehand
    public static String validateFactionName(String factionName, boolean alreadyExistsInvalid) {
        if (!(ConfigState.factionNameMin <= factionName.length() && factionName.length() <= ConfigState.factionNameMax))
            return "§cFaction name must be between §f%d and %d§c characters!§r"
                    .formatted(ConfigState.factionNameMin, ConfigState.factionNameMax);
        if (alreadyExistsInvalid && Faction.get(factionName) != null)
            return "§cA faction with that name already exists!§r";

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}0-9', ()./_-]").matcher(factionName);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following characters are not allowed in a faction name: §r" + String.join("", illegalChars) + "§r";
        if (factionName.chars().anyMatch(ch -> ch < 32))
            return "§cFaction name must not contain any control characters!";

        // faction name is valid
        return null;
    }
}
