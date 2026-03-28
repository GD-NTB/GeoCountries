package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcPurgeUsername extends GeoCommand {

    public gcPurgeUsername(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Purges a PlayerProfile by username.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the name of the player who you want to purge!");
            return;
        }

        String username = args[0];

        PlayerProfile playerProfile = PlayerProfile.get(username);
        if (playerProfile == null) {
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
        PlayerProfile playerProfile = PlayerProfile.get(args[0]);
        playerProfile.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + playerProfile.getUsername() + "§a.");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUsernames(true) : List.of();
    }
}
