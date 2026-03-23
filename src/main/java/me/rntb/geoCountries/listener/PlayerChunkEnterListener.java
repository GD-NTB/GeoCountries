package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerChunkEnterListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        int fromX = event.getFrom().getBlockX() >> 4;
        int fromZ = event.getFrom().getBlockZ() >> 4;
        int toX = event.getTo().getBlockX() >> 4;
        int toZ = event.getTo().getBlockZ() >> 4;

        // if didn't change chunk, escape
        if (fromX == toX && fromZ == toZ)
            return;

        ClaimChunk fromClaimChunk = ClaimChunk.get(fromX, fromZ);
        ClaimChunk toClaimChunk = ClaimChunk.get(toX, toZ);

        Player player = event.getPlayer();
        Country playerCountry = PlayerProfile.get(player).getCitizenshipObject();

        // [...] -> claimed
        if (toClaimChunk != null) {
            // get chat colour
            String colour;
            if (playerCountry != null)
                colour = playerCountry.getChatColourBetweenOtherCountry(toClaimChunk.getOwnerObject());
            else
                colour = "§f";

            // send actionbar
            // unclaimed -> claimed or claimed -> claimed
            if (fromClaimChunk == null || !fromClaimChunk.getOwnerObject().equals(toClaimChunk.getOwnerObject()))
                player.sendActionBar(Component.text(colour + "You are now in the territory of §6" + toClaimChunk.getOwnerObject().getNameAndFaction() + colour + "."));
        }

        // claimed -> unclaimed
        else if (fromClaimChunk != null) {
            // get chat colour
            String colour;
            if (playerCountry != null)
                colour = playerCountry.getChatColourBetweenOtherCountry(fromClaimChunk.getOwnerObject());
            else
                colour = "§f";

            // send actionbar
            player.sendActionBar(Component.text(colour + "You are no longer in the territory of §6" + fromClaimChunk.getOwnerObject().getNameAndFaction() + colour + "."));
        }
    }
}
