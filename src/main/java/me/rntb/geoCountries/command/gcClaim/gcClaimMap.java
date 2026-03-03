package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// todo: use component for hover over and stuff
public class gcClaimMap extends GeoCommand {

    public gcClaimMap(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Claims the chunk where you are standing.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country playerCountry = playerProfile.getCitizenship();

        StringBuilder message = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                                  "§6========== CLAIMS MAP ==========\n");


        Chunk chunk = player.getChunk();
        // get width and height from args
        int width;
        if (args.length >= 1) {
            try {
                width = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                ChatUtil.sendPrefixedMessage(sender, "§cFirst argument §f" + args[0] + "§c is not an integer!");
                return;
            }
            width = Math.clamp(width, 3, 48);
        }
        else
            width = 30;

        int height;
        if (args.length >= 2) {
            try {
                height = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.sendPrefixedMessage(sender, "§cSecond argument §f" + args[1] + "§c is not an integer!");
                return;
            }
            height = Math.clamp(height, 3, 18);
        }
        else
            height = 8;

        // draw shite
        int halfWidth = (int) (width*0.5);
        int halfHeight = (int) (height*0.5);

        for (int off_z = -halfHeight; off_z < halfHeight + 1; off_z++) {
            for (int off_x = -halfWidth; off_x < halfWidth + 1; off_x++) {
                int x = chunk.getX() + off_x;
                int z = chunk.getZ() + off_z;
                ClaimChunk claim = ClaimChunk.get(Chunk.getChunkKey(x, z));

                // player marker
                if (off_x == 0 && off_z == 0) {
                    message.append("§e+");
                    continue;
                }

                // empty
                if (claim == null || claim.owner == null) {
                    message.append("§8-");
                    continue;
                }

                // claim
                if (playerCountry != null && claim.owner.equals(playerCountry.uuid))
                    message.append("§a#");
                else
                    message.append("§f#");
            }
            message.append("\n");
        }

        message.append("§6==============================");

        ChatUtil.sendPrefixedMessage(sender, String.valueOf(message));
    }
}
