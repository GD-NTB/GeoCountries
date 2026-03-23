package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gcClaimOne extends GeoCommand {

    public gcClaimOne(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Claims the chunk where you are standing.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);
        Country senderCountry = playerProfile.getCitizenshipObject();

        if (senderCountry == null || playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to claim chunks! " + playerProfile.getChunkString());
            return;
        }

        Chunk chunk = player.getChunk();
        if (!chunk.getWorld().getName().equals(ConfigState.claimWorld)) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou cannot claim here! " + playerProfile.getChunkString());
            return;
        }

        long chunkKey = chunk.getChunkKey();
        ClaimChunk claimChunk = ClaimChunk.get(chunkKey);
        if (claimChunk != null) {
            Country owner = claimChunk.getOwnerObject();
            if (owner.equals(senderCountry))
                ChatUtil.sendPrefixedMessage(sender, "§cThis chunk has already been claimed by your country! " + playerProfile.getChunkString());
            else
                ChatUtil.sendPrefixedMessage(sender, "§cThis chunk has already been claimed by §f" + owner.getName() + "§c! " + playerProfile.getChunkString());
            return;
        }

        claimChunk = new ClaimChunk(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID(), senderCountry.getUUID());
        claimChunk.register();

        ChatUtil.sendPrefixedMessage(sender, "§aClaimed the chunk! " + playerProfile.getChunkString());
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
