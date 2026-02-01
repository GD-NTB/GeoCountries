package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class gcCountryCitizens {

    public static void onCommand(CommandSender sender, String[] args) {
        Country country;
        int index = 1;
        String countryName = "";

        // if no args, country = player's country
        if (args.length == 0) {
            PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
            country = playerProfile.getCitizenship();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY CITIZENS ==========
                                                     §cYou do not have citizenship of any country.
                                                     §cDo §f/gc country citizens [country]§c to get a list of a country's citizens.
                                                     §6=================================""");
                return;
            }
        }
        else {
            // if greater than 2, we have a page number and country name
            // /gc country citizens [pagenumber] [countryname]
            String[] countryNameArgs;
            if (args.length >= 2) {
                try {
                    index = Integer.parseInt(args[0]);
                    countryNameArgs = Arrays.copyOfRange(args, 1, args.length);
                } catch (NumberFormatException ignored) {
                    countryNameArgs = args;
                }
            }
            // /gc country citizens [countryname]
            else
                countryNameArgs = args;

            countryName = String.join(" ", countryNameArgs);

            country = Country.byName.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== COUNTRY CITIZENS =========="))
               .append(Component.newline());

        int effectiveIndex = 0;
        int pageCount = 0;
        String commandForPrevious = "";
        String commandForNext = "";

        int citizenCount = country.citizenCount();
        if (citizenCount == 0) {
            message.append(Component.text("§cThere are no citizens of this country.\n"));
            return;
        }
        else {
            message.append(Component.text("§e%s§f has §e%d§f citizen%s:\n"
                      .formatted(country.name, citizenCount, StringUtil.leadingS(citizenCount))));
            StringBuilder citizensText = new StringBuilder();
            for (PlayerProfile citizen : country.citizensSortedByRank()) {
                citizensText.append("§f> §a%s§f (§e%s§f)\n"
                                    .formatted(citizen.username, citizen.getRankString()));
            }
            // calculate page
            Pagination page = Pagination.paginate(String.valueOf(citizensText), "\n", index, 20);
            message.append(Component.text(page.text))
                   .append(Component.newline());
            pageCount = page.pageCount;
            effectiveIndex = page.index;
            commandForPrevious = "/gc country citizens " + (effectiveIndex-1) + " " + country.name;
            commandForNext = "/gc country citizens " + (effectiveIndex+1) + " " + country.name;
        }

        message.append(Component.text("§6====================================="))
               .append(Component.newline())
               .append(Component.text("               "));

        // append chat page control buttons
        message.append(ChatUtil.chatPageControlButtons(commandForPrevious, commandForNext, effectiveIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }
}
