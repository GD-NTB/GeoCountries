package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.PlayerRank;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.service.RankService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcAdminSetPlayerRank extends GeoCommand {

    public gcAdminSetPlayerRank(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Sets a player's rank.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to change the rank of!");
            return;
        }

        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new rank you want the player to have!");
            return;
        }

        String playerName = args[0];
        PlayerProfile player = PlayerProfile.get(playerName);

        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // get rank
        PlayerRank rank;
        try {
            rank = PlayerRank.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            ChatUtil.sendPrefixedMessage(sender, "§cRank §f" + args[1] + "§c could not be found!");
            return;
        }

        // set rank
        switch (rank) {
            case LEADER:
                RankService.promoteToLeader(player);
                break;
            case CITIZEN:
                if (player.rank == PlayerRank.LEADER)
                    RankService.demoteFromLeader(player);
                break;
            case NONE:
                CitizenshipService.leaveCountry(player);
                break;
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet player rank!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> PlayerProfile.allAsUsernames(true);
            case 2 -> EnumUtil.enumToStringList(PlayerRank.class);
            default -> List.of();
        };
    }
}
