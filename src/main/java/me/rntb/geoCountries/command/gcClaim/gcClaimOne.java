package me.rntb.geoCountries.command.gcClaim;

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

public class gcClaimOne extends GeoCommand {

    public gcClaimOne(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Claims the chunk where you are standing.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);
        Country country = playerProfile.getCitizenship();

        if (country == null || playerProfile.position != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to claim chunks! " + playerProfile.getChunkString());
            return;
        }

        Chunk chunk = player.getChunk();

        long chunkKey = chunk.getChunkKey();
        ClaimChunk claimChunk = ClaimChunk.get(chunkKey);
        if (claimChunk != null) {
            ChatUtil.sendPrefixedMessage(sender, "§cThis chunk has already been claimed by §f" + Country.get(claimChunk.owner).name + "§c! " + playerProfile.getChunkString());
            return;
        }

        claimChunk = new ClaimChunk(chunk.getChunkKey(), chunk.getX(), chunk.getZ(), country.uuid);
        claimChunk.register();

        ChatUtil.sendPrefixedMessage(sender, "§aClaimed the chunk! " + playerProfile.getChunkString());
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).position == Position.LEADER;
    }
}
