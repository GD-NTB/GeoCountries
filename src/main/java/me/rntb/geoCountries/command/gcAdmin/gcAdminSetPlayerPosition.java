package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.service.CountryService;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcAdminSetPlayerPosition extends GeoCommand {

    public gcAdminSetPlayerPosition(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Sets a player's position.");
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
        PlayerProfile playerProfile = PlayerProfile.get(playerName);

        if (playerProfile == null) {
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
                CountryService.promoteToLeader(playerProfile.getCitizenshipObject(), playerProfile);
                break;
            case CITIZEN:
                if (playerProfile.getPosition() == Position.LEADER) {
                    Country country = playerProfile.getCitizenshipObject();
                    CountryService.demoteFromLeader(country, playerProfile, null);
                    IntegrationManager.onStyleUpdate(country); // hacky, but this is an admin command

                    ChatUtil.sendPrefixedMessage(sender, "§aSet new leader to random citizen or dissolved.");
                }
                break;
            case NONE:
                CountryService.leaveCountry(playerProfile.getCitizenshipObject(), playerProfile);
                break;
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet player position to §f" + position.name() + "§a!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }
}
