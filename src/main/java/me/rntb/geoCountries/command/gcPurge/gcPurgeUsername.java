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

public class gcPurgeUsername extends GeoCommand {

    public gcPurgeUsername(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges a PlayerProfile by username.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What is the username of the player you want to purge?");
            // start waiting for response
            Response.startWaiting(PlayerProfile.get(sender).getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else
            onResponse(sender, args[0]);
    }

    private void onResponse(CommandSender sender, String username) {
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
