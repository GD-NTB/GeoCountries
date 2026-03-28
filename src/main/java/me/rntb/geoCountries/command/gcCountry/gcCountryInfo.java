package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCountryInfo extends GeoCommand {

    public gcCountryInfo(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Displays info about your/any country.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Country country;
        // if no args, get player country
        if (args.length == 0) {
            PlayerProfile playerProfile = PlayerProfile.get(sender);
            country = playerProfile.getCitizenshipObject();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY INFO ==========
                                                     §cYou do not have citizenship of any country.
                                                     §cDo §f/gc country create§c to create a country, or
                                                     §cdo §f/gc citizenship apply§c to apply for citizenship of one.
                                                     §6=================================""");
                return;
            }
        }
        // else get specific country
        else {
            String countryName = String.join(" ", args);
            country = Country.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        String countryMotto = country.getSettings().get("motto");

        PlayerProfile leader = country.getLeaderObject();

        String factionString;
        if (country.hasFaction()) {
            Faction faction = country.getFactionObject();
            factionString = "%s §f(§e%s§f)"
                            .formatted(faction.getName(),
                                       faction.getLeader().equals(country.getUUID()) ? "LEADER" : "Member");
        }
        else
            factionString = "§cNone";

        int size = country.getClaimChunksCount();
        float sizePercent;
        if (size == 0 || ClaimChunk.all.isEmpty())
            sizePercent = 0;
        else
            sizePercent = ((float) size / ClaimChunk.all.size())*100;

        long daysAgo = TimeUtil.daysAgo(country.getTimeCreated());

        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== COUNTRY INFO ==========
                         §a%s§f
                         §f> §eMotto§f: %s
                         §f> §eFaction§f: §3%s
                         §f> §eLeader§f: %s
                         §f> §eCitizens§f: %s
                         §f> §eSize§f: %d chunk%s (%.1f%%)
                         §f> Created on §2%s §8(%s day%s ago)
                         §6================================="""
                         .formatted(country.getNameAndFaction(),
                                    !countryMotto.equals("null") ? countryMotto : "§cNone",
                                    factionString,
                                    leader != null ? leader.getUsername() : "§cNone",
                                    country.getCitizenCount(),
                                    size, StringUtil.leadingS(size), sizePercent,
                                    country.getTimeCreatedAsString(), daysAgo, StringUtil.leadingS(daysAgo));

        ChatUtil.sendPrefixedMessage(sender, message);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.allAsNames(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).hasCitizenship();
    }
}
