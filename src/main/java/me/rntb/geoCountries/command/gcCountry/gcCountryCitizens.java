package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.PageNumberAndArgs;
import me.rntb.geoCountries.type.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// todo: display time last online
public class gcCountryCitizens extends GeoCommand {

    public gcCountryCitizens(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all citizens of your/any country.");
        addAlias("members");
    }

    private static final int ENTRIES_PER_PAGE = 15;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // parse args
        PageNumberAndArgs pageNumberAndArgs = PageNumberAndArgs.parse(args);
        int wantedPage = pageNumberAndArgs.pageNumber();
        String[] countryNameArgs = pageNumberAndArgs.args();

        // get country object
        Country country;
        // if no country specified, use sender's country
        if (countryNameArgs == null) {
            PlayerProfile playerProfile = PlayerProfile.get(sender);
            country = playerProfile.getCitizenshipObject();
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== COUNTRY CITIZENS ==========
                                                     §cYou are not a citizen of any country.
                                                     §cDo §f/gc country citizens [country]§c to get a list of a country's citizens.
                                                     §6=====================================""");
                return;
            }
        }
        // else use specified country
        else {
            String countryName = String.join(" ", pageNumberAndArgs.args());
            country = Country.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }
        }

        // build text
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== COUNTRY CITIZENS =========="))
               .append(Component.newline());

        // pagination fields
        int pageIndex = 0, pageCount = 0;

        int citizenCount = country.getCitizenCount();
        if (citizenCount == 0)
            message.append(Component.text("§cThere are no citizens of this country."))
                   .append(Component.newline());
        else {
            // append title
            message.append(Component.text("§e%s§f has §e%d§f citizen%s:"
                                          .formatted(country.getName(),
                                                     citizenCount, StringUtil.leadingS(citizenCount))))
                   .append(Component.newline());

            // calculate required page of country.getCitizensSorted
            Pagination pagination = Pagination.paginate(country.getCitizensSorted(), wantedPage, ENTRIES_PER_PAGE);
            List<PlayerProfile> citizens = (List<PlayerProfile>) pagination.content();
            pageIndex = pagination.pageIndex();
            pageCount = pagination.pageCount();

            // iterate through page
            for (PlayerProfile citizen : citizens) {
                message.append(Component.text("§f> §a%s§f (§e%s§f)"
                                              .formatted(citizen.getUsername(), citizen.getPositionString())))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6====================================="))
               .append(Component.newline())
               .append(Component.text("               "))
               // append chat page control buttons
               .append(ChatUtil.getPaginationButtons("gc country citizens " + (pageIndex - 1) + " " + country.getName(),
                                                     "gc country citizens " + (pageIndex + 1) + " " + country.getName(),
                                                     pageIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Country.getAllAsNames(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).hasCitizenship();
    }
}
