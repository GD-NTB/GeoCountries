package me.rntb.geoCountries.command.gcUnclaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gcUnclaimOne extends GeoCommand {

    public gcUnclaimOne(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Unclaims the chunk where you are standing.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        if (playerProfile.position != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to unclaim chunks!");
            return;
        }

        Country country = playerProfile.getCitizenship();
        Player player = playerProfile.getOnlinePlayer();
        Chunk chunk = player.getChunk();

        long chunkKey = chunk.getChunkKey();
        ClaimChunk claimChunk = ClaimChunk.get(chunkKey);
        if (claimChunk == null || claimChunk.owner != country.uuid) {
            ChatUtil.sendPrefixedMessage(sender, "§cThis chunk is not part of your country's claim!");
            return;
        }

        claimChunk.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aUnclaimed the chunk!§6 (%d, %d)"
                .formatted(claimChunk.x, claimChunk.z));
    }
}
