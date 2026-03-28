package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFactionList extends GeoCommand {

    public gcFactionList(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all factions on the server.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // todo: pages
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== FACTION LIST ==========\n");

        if (Faction.all.isEmpty()) {
            sb.append("§cThere are no factions.\n");
        }
        else {
            for (Faction faction : Faction.all) {
                Country leader = faction.getLeaderObject();
                PlayerProfile leaderOfLeader = null;
                if (leader != null)
                    leaderOfLeader = leader.getLeaderObject();
                sb.append("§f> §3%s§f (§eLeader§f: %s (§e%s§f), §eMembers§f: %s§f)\n"
                          .formatted(faction.getName(),
                                     leader != null ? leader.getName() : "§cNone§f",
                                     leaderOfLeader != null ? leaderOfLeader.getUsername() : "§cNone§f",
                                     faction.getMemberCount()));
            }
        }
        sb.append("§6================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
