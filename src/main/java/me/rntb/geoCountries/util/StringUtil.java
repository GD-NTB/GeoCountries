package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static String leadingS(long count) {
        return count == 1 ? "" : "s";
    }
    public static String uppercaseLeadingS(long count) {
        return count == 1 ? "" : "S";
    }

    // ---------- string validation ----------

    // ----- country -----
    // country name should be trimmed beforehand
    public static String validateCountryName(String countryName, boolean alreadyExistsInvalid) {
        if (!(ConfigState.CountryNameMin <= countryName.length() && countryName.length() <= ConfigState.CountryNameMax))
            return "§cCountry name must be between §f%d and %d§c characters!§r"
                    .formatted(ConfigState.CountryNameMin, ConfigState.CountryNameMax);
        if (alreadyExistsInvalid && Country.byName.get(countryName) != null)
            return "§cA country with that name already exists!§r";

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}0-9', ()./_-]").matcher(countryName);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following characters are not allowed in a country name: §r" + String.join("", illegalChars);
        if (countryName.chars().anyMatch(ch -> ch < 32))
            return "§cCountry name must not contain any control characters!";

        // country name is valid
        return null;
    }

    // ----- response -----
    // response should be trimmed beforehand
    public static String validateResponse(String response) {
        if (!(ConfigState.ChatResponseMin <= response.length() && response.length() <= ConfigState.ChatResponseMax))
            return "§cChat message must be between §f%d and %d§c characters!§r"
                    .formatted(ConfigState.ChatResponseMin, ConfigState.ChatResponseMax);

        // illegal characters
        Matcher m = Pattern.compile("[^\\p{L}\\p{N} ,.?!;:£$%^&*'()=+_#\\[\\]/\\-]").matcher(response);
        Set<String> illegalChars = new LinkedHashSet<>();
        while (m.find())
            illegalChars.add(m.group());
        if (!illegalChars.isEmpty())
            return "§cThe following character(s) you typed are not allowed: §r" + String.join("", illegalChars);
        if (response.chars().anyMatch(ch -> ch < 32))
            return "§cChat message must not contain any control characters!";

        // response is valid
        return null;
    }
}
