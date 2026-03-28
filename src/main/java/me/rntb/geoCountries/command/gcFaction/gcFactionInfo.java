package me.rntb.geoCountries.command.gcFaction;

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

public class gcFactionInfo extends GeoCommand {

    public gcFactionInfo(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Displays info about your/any faction.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Faction faction;
        // if no args, get player faction
        if (args.length == 0) {
            PlayerProfile playerProfile = PlayerProfile.get(sender);
            faction = playerProfile.getFactionObject();
            if (faction == null) {
                ChatUtil.sendPrefixedMessage(sender, ChatUtil.newlineIfPrefixIsEmpty() +
                                                     """
                                                     §6========== FACTION INFO ==========
                                                     §cYou are not in a faction.
                                                     §cDo §f/gc faction create§c to create one.
                                                     §6================================""");
                return;
            }
        }
        // else get specific faction
        else {
            String factionName = String.join(" ", args);
            faction = Faction.get(factionName);
            if (faction == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cFaction §f" + factionName + "§c does not exist!");
                return;
            }
        }

        Country leader = faction.getLeaderObject();
        PlayerProfile leaderOfLeader = null;
        if (leader != null)
            leaderOfLeader = leader.getLeaderObject();

        int totalSize = faction.getTotalClaimChunks();
        float totalSizePercent;
        if (totalSize == 0 || ClaimChunk.all.isEmpty())
            totalSizePercent = 0;
        else
            totalSizePercent = ((float) totalSize / ClaimChunk.all.size())*100;

        long daysAgo = TimeUtil.daysAgo(faction.getTimeCreated());
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== FACTION INFO ==========
                         §3%s§f
                         §f> §eLeader§f: %s §f(§e%s§f)
                         §f> §eMembers§f: %d
                         §f> §eTotal size§f: %d chunk%s (%.1f%%)
                         §f> Created on §2%s §8(%s day%s ago)
                         §6================================="""
                         .formatted(faction.getName(),
                                    leader != null ? leader.getName() : "§cNone",
                                    leaderOfLeader != null ? leaderOfLeader.getUsername() : "§cNone",
                                    faction.getMemberCount(),
                                    totalSize, StringUtil.leadingS(totalSize), totalSizePercent,
                                    faction.getTimeCreatedAsString(), daysAgo, StringUtil.leadingS(daysAgo));

        ChatUtil.sendPrefixedMessage(sender, message);
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
