package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.PageNumberAndArgs;
import me.rntb.geoCountries.type.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCountryList extends GeoCommand {

    public gcCountryList(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all countries on the server.");
    }

    private static final int ENTRIES_PER_PAGE = 15;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // parse args
        PageNumberAndArgs pageNumberAndArgs = PageNumberAndArgs.parse(args);
        int wantedPage = pageNumberAndArgs.pageNumber();

        Pagination pagination = Pagination.EMPTY;

        // build text
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== COUNTRY LIST =========="))
               .append(Component.newline());

        if (Country.getAll().isEmpty())
            message.append(Component.text("§cThere are no countries."))
                   .append(Component.newline());
        else {
            // calculate required page of Country.all
            pagination = Pagination.paginate(Country.getAll(), wantedPage, ENTRIES_PER_PAGE);
            List<Country> countries = (List<Country>) pagination.content();

            // iterate through page
            for (Country country : countries) {
                PlayerProfile leader = country.getLeaderObject();
                int citizens = country.getCitizenCount();

                message.append(Component.text("§f> §a%s§f (§eLeader§f: %s, §eCitizens§f: %s§f)"
                                              .formatted(country.getNameAndFaction(),
                                                         leader != null ? country.getLeaderObject().getUsername() : "§cNone§f",
                                                         citizens != 0 ? citizens : "§c0§f")))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6================================"))
               .append(Component.newline())
               .append(Component.text("               "))
               // append chat page control buttons
               .append(ChatUtil.getPaginationButtons("gc country list " + (pagination.pageIndex() - 1),
                                                     "gc country list " + (pagination.pageIndex() + 1),
                                                     pagination.pageIndex(), pagination.pageCount()));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }
}
