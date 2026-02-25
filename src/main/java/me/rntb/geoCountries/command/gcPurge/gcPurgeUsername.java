package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcPurgeUsername extends GeoCommand {

    public gcPurgeUsername(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges a PlayerProfile by username.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou need to specify the username of the player as it appears in the data collections.");
            return;
        }

        String username = args[0];
        PlayerProfile player = PlayerProfile.byUsername.get(username);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + username + "§c could not be found!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { username }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.byUsername.get(args[0]);
        PlayerProfile.delete(player);

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + player.username + "§a.");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUsernames(true) : List.of();
    }
}
