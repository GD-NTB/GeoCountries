package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CitizenshipService;
import me.rntb.geoCountries.service.PositionService;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.EnumUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// todo: use Response for insufficient args
public class gcAdminSetPlayerPosition extends GeoCommand {

    public gcAdminSetPlayerPosition(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Sets a player's position.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to change the position of!");
            return;
        }

        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new position you want the player to have!");
            return;
        }

        String playerName = args[0];
        PlayerProfile player = PlayerProfile.get(playerName);

        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // get position
        Position position;
        try {
            position = Position.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            ChatUtil.sendPrefixedMessage(sender, "§cPosition §f" + args[1] + "§c could not be found!");
            return;
        }

        // set position
        switch (position) {
            case LEADER:
                PositionService.promoteToLeader(player);
                break;
            case CITIZEN:
                if (player.getPosition() == Position.LEADER)
                    PositionService.demoteFromLeader(player);
                break;
            case NONE:
                CitizenshipService.leaveCountry(player);
                break;
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet player position to §f" + position.name() + "§a!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> PlayerProfile.allAsUsernames(true);
            case 2 -> EnumUtil.enumToStringList(Position.class);
            default -> List.of();
        };
    }
}
