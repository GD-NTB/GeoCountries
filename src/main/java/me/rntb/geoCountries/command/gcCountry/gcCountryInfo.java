package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.DateUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCountryInfo extends GeoCommand {

    public gcCountryInfo(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Displays info about your/any particular country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Country country;
        // if no args, get player country
        if (args.length == 0) {
            PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
            country = playerProfile.getCitizenship();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY INFO ==========
                                                     §cYou do not have citizenship of any country.
                                                     §cDo §f/gc country create§f to create a country, or
                                                     §cdo §f/gc citizenship apply§c to apply to apply for citizenship of one.
                                                     §6=================================""");
                return;
            }
        }
        // else get specific country
        else {
            String countryName = String.join(" ", args);
            country = Country.byName.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        PlayerProfile leader = country.getLeader();
        long daysAgo = DateUtil.daysAgo(country.timeCreated);

        String countryMotto = country.settings.get("motto");

        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== COUNTRY INFO ==========
                         §a%s§f
                         §f> §eMotto§f: %s
                         §f> §eLeader§f: %s
                         §f> §eCitizens§f: %s
                         §f> Created on §2%s §8(%s day%s ago)
                         §6================================="""
                         .formatted(country.name,
                                    !countryMotto.equals("null") ? countryMotto : "§cNone",
                                    leader != null ? leader.username : "§cNone",
                                    country.citizenCount(),
                                    country.timeCreatedAsString(),
                                    daysAgo, StringUtil.leadingS(daysAgo));
        ChatUtil.sendPrefixedMessage(sender, message);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsStrings(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.byCommandSender(sender).hasCitizenship();
    }
}
