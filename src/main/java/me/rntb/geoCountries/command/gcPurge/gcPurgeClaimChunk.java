package me.rntb.geoCountries.command.gcPurge;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcPurgeClaimChunk extends GeoCommand {

    public gcPurgeClaimChunk(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Purges all ClaimChunk data collections.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        int count = ClaimChunk.purge();
        ChatUtil.sendPrefixedMessage(sender, "§aPurged §f" + count + "§a ClaimChunk" + StringUtil.leadingS(count) + ".");
    }
}
