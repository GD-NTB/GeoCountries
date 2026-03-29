package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.PageNumberAndArgs;
import me.rntb.geoCountries.type.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcFactionList extends GeoCommand {

    public gcFactionList(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all factions on the server.");
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
               .append(Component.text("§6========== FACTION LIST =========="))
               .append(Component.newline());

        if (Faction.getAll().isEmpty()) {
            message.append(Component.text("§cThere are no factions."))
                   .append(Component.newline());
        }
        else {
            // calculate required page of Faction.all
            pagination = Pagination.paginate(Faction.getAll(), wantedPage, ENTRIES_PER_PAGE);
            List<Faction> factions = (List<Faction>) pagination.content();

            // iterate through page
            for (Faction faction : factions) {
                Country leader = faction.getLeaderObject();
                PlayerProfile leaderOfLeader = null;
                if (leader != null)
                    leaderOfLeader = leader.getLeaderObject();
                message.append(Component.text("§f> §3%s§f (§eLeader§f: %s (§e%s§f), §eMembers§f: %s§f)"
                                              .formatted(faction.getName(),
                                                         leader != null ? leader.getName() : "§cNone§f",
                                                         leaderOfLeader != null ? leaderOfLeader.getUsername() : "§cNone§f",
                                                         faction.getMemberCount())))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6================================"))
               .append(Component.newline())
               .append(Component.text("               "))
               // append chat page control buttons
               .append(ChatUtil.getPaginationButtons("gc faction list " + (pagination.pageIndex() - 1),
                                                     "gc faction list " + (pagination.pageIndex() + 1),
                                                     pagination.pageIndex(), pagination.pageCount()));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }
}
