package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
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

public class gcFactionMembers extends GeoCommand {

    public gcFactionMembers(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all faction of your/any country.");
    }

    private static final int ENTRIES_PER_PAGE = 15;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // parse args
        PageNumberAndArgs pageNumberAndArgs = PageNumberAndArgs.parse(args);
        int wantedPage = pageNumberAndArgs.pageNumber();
        String[] factionNameArgs = pageNumberAndArgs.args();

        // get faction object
        Faction faction;
        // if no faction specified, use sender's faction
        if (factionNameArgs == null) {
            PlayerProfile playerProfile = PlayerProfile.get(sender);
            faction = playerProfile.getFactionObject();
            if (faction == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== FACTION CITIZENS ==========
                                                     §cYou are not a member of any faction.
                                                     §cDo §f/gc faction members [faction]§c to get a list of a faction's members.
                                                     §6====================================""");
                return;
            }
        }
        // else use specified faction
        else {
            String factionName = String.join(" ", pageNumberAndArgs.args());
            faction = Faction.get(factionName);
            if (faction == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cFaction §f" + factionName + "§c does not exist!");
                return;
            }
        }

        // build text
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== FACTION CITIZENS =========="))
               .append(Component.newline());

        // pagination fields
        int pageIndex = 0, pageCount = 0;

        int memberCount = faction.getMemberCount();
        if (memberCount == 0)
            message.append(Component.text("§cThere are no members of this faction."))
                   .append(Component.newline());
        else {
            // append title
            message.append(Component.text("§3%s§f has §e%d§f member%s:"
                                          .formatted(faction.getName(),
                                                     memberCount, StringUtil.leadingS(memberCount))))
                   .append(Component.newline());

            // calculate required page of faction.getMembersSorted
            Pagination pagination = Pagination.paginate(faction.getMembersSorted(), wantedPage, ENTRIES_PER_PAGE);
            List<Country> members = (List<Country>) pagination.content();
            pageIndex = pagination.pageIndex();
            pageCount = pagination.pageCount();

            for (Country member : members) {
                message.append(Component.text("§f> §a%s§f (§e%s§f)"
                                    .formatted(member.getName(),
                                               member.isFactionLeader() ? "Leader" : "Member")))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6===================================="))
               .append(Component.newline())
               .append(Component.text("               "))
               // append chat page control buttons
               .append(ChatUtil.getPaginationButtons("gc faction members " + (pageIndex - 1) + " " + faction.getName(),
                                                     "gc faction members " + (pageIndex + 1) + " " + faction.getName(),
                                                     pageIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? Faction.allAsNames(true) : List.of();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).hasFaction();
    }
}
