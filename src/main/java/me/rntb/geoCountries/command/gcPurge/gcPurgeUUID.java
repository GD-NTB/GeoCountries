package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;

public class gcPurgeUUID extends SubSubCommand {

    public gcPurgeUUID(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou need to specify the UUID of the player as it appears in the data collections.");
            return;
        }

        String uuid = args[0];
        PlayerProfile player = PlayerProfile.byUUIDString(uuid);
        if (player == null) {
            ChatUtil.sendPrefixedMessage(sender, "§UUID §f" + uuid + "§c could not be found!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { uuid }),
                                  true);
    }

    @Override
    public void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.byUUIDString(args[0]);
        PlayerProfile.delete(player);

        assert player != null;
        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + player.username + "§a.");
    }
}
