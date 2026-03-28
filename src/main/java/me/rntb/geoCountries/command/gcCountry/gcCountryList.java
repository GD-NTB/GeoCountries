package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
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
        // get page number if specified
        int wantedPage = 1;
        if (args.length >= 1) {
            try {
                wantedPage = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) { }
        }

        // pagination fields
        int pageIndex = 0, pageCount = 0;

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
            Pagination pagination = Pagination.paginate(Country.getAll(), wantedPage, ENTRIES_PER_PAGE);
            List<Country> countries = (List<Country>) pagination.content();
            pageIndex = pagination.pageIndex();
            pageCount = pagination.pageCount();

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
               .append(ChatUtil.getPaginationButtons("gc country list " + (pageIndex - 1),
                                                     "gc country list " + (pageIndex + 1),
                                                     pageIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }
}
