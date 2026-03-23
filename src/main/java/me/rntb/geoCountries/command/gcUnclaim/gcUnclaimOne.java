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

    public gcUnclaimOne(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Unclaims the chunk where you are standing.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);
        Chunk chunk = player.getChunk();
        long chunkKey = chunk.getChunkKey();

        if (playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to unclaim chunks! " + playerProfile.getChunkString());
            return;
        }

        Country country = playerProfile.getCitizenshipObject();
        ClaimChunk claimChunk = ClaimChunk.get(chunkKey);
        if (claimChunk == null || !claimChunk.getOwner().equals(country.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cThis chunk is not part of your country's claim! " + playerProfile.getChunkString());
            return;
        }

        claimChunk.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aUnclaimed the chunk! " + playerProfile.getChunkString());
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
