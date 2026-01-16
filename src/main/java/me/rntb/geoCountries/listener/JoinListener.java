package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        PlayerProfile playerProfile = PlayerProfile.byUUID.get(player.getUniqueId());

        // if new player, create them a new PlayerProfile
        if (playerProfile == null) {
            // create and register new PlayerProfile for this player
            playerProfile = new PlayerProfile(player);
            PlayerProfile.addNew(playerProfile);
        }

        // update last known username to their current username
        playerProfile.username = player.getName();
    }
}
