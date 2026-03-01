package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcPurgeUUID extends GeoCommand {

    public gcPurgeUUID(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges a PlayerProfile by UUID.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What is the UUID of the player you want to purge?");
            // start waiting for response
            Response.startWaiting(PlayerProfile.get(sender).uuid,
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else
            onResponse(sender, args[0]);
    }

    private void onResponse(CommandSender sender, String uuidString) {
        PlayerProfile player = PlayerProfile.byUUIDString(uuidString);
        if (player == null) {
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
        PlayerProfile player = PlayerProfile.byUUIDString(args[0]);
        assert player != null;

        player.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aPurged player §f" + player.username + "§a.");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? PlayerProfile.allAsUUIDStrings() : List.of();
    }
}
