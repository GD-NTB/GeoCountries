package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class gcFactionMembers extends GeoCommand {

    public gcFactionMembers(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists all faction of your/any country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Faction faction;
        int index = 1;
        String factionName;

        // if no args, faction = player's faction
        if (args.length == 0) {
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
        else {
            // if greater than 2, we have a page number and faction name
            // /gc faction members [pagenumber] [factionname]
            String[] factionNameArgs;
            if (args.length >= 2) {
                try {
                    index = Integer.parseInt(args[0]);
                    factionNameArgs = Arrays.copyOfRange(args, 1, args.length);
                } catch (NumberFormatException ignored) {
                    factionNameArgs = args;
                }
            }
            // /gc faction members [factionname]
            else
                factionNameArgs = args;

            factionName = String.join(" ", factionNameArgs);

            faction = Faction.get(factionName);
            if (faction == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + factionName + "§c does not exist!");
                return;
            }
        }

        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== FACTION CITIZENS =========="))
               .append(Component.newline());

        int effectiveIndex = 0, pageCount = 0;
        String commandForPrevious = "", commandForNext = "";

        int memberCount = faction.getMemberCount();
        if (memberCount == 0) {
            message.append(Component.text("§cThere are no members of this faction.\n"));
        }
        else {
            message.append(Component.text("§3%s§f has §e%d§f member%s:\n"
                                          .formatted(faction.getName(),
                                                     memberCount, StringUtil.leadingS(memberCount))));
            StringBuilder membersText = new StringBuilder();
            for (Country member : faction.getMembersSorted()) {
                membersText.append("§f> §a%s§f (§e%s§f)\n"
                                    .formatted(member.getName(),
                                               member.getUUID().equals(faction.getLeader()) ? "LEADER" : "Member"));
            }
            // calculate page
            Pagination page = Pagination.paginate(String.valueOf(membersText), "\n", index, 20);
            message.append(Component.text(page.text))
                   .append(Component.newline());
            pageCount = page.pageCount;
            effectiveIndex = page.index;
            commandForPrevious = "/gc faction members " + (effectiveIndex-1) + " " + faction.getName();
            commandForNext = "/gc faction members " + (effectiveIndex+1) + " " + faction.getName();
        }

        message.append(Component.text("§6===================================="))
               .append(Component.newline())
               .append(Component.text("               "));

        // append chat page control buttons
        message.append(ChatUtil.chatPageControlButtons(commandForPrevious, commandForNext, effectiveIndex, pageCount));

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
