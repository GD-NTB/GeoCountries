package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcClaimInfo extends GeoCommand {

    public gcClaimInfo(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Displays info about the chunk you're standing on.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        ClaimChunk claimChunk = playerProfile.getClaimChunk();

        if (claimChunk == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou are not standing in any claim! " + playerProfile.getChunkString());
            return;
        }

        long daysAgo = TimeUtil.daysAgo(claimChunk.getTimeCreated());
        String message = ChatUtil.newlineIfPrefixIsEmpty() +
                         """
                         §6========== CHUNK INFO ==========
                         §aChunk at §f(%s, %s)
                         §f> §eClaimed by §f%s
                         §f> Claimed §2%s day%s ago
                         §6================================"""
                        .formatted(claimChunk.getX(), claimChunk.getZ(),
                                   claimChunk.getOwnerObject().getNameAndFaction(),
                                   daysAgo, StringUtil.leadingS(daysAgo));
        ChatUtil.sendPrefixedMessage(sender, message);
    }
}
