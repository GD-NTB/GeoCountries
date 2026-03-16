package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcPurgeUUID extends GeoCommand {

    public gcPurgeUUID(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges a PlayerProfile by UUID.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the UUID of the player who you want to purge!");
            return;
        }

        String uuidString = args[0];

        PlayerProfile playerProfile = PlayerProfile.byUUIDString(uuidString);
        if (playerProfile == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cUUID §f" + uuidString + "§c could not be found!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { uuidString }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.byUUIDString(args[0]);
        assert playerProfile != null;

        playerProfile.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + playerProfile.getUsername() + "§a.");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUUIDStrings() : List.of();
    }
}
