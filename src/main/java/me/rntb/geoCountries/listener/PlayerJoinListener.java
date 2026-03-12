package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile playerProfile = PlayerProfile.get(player);

        // if new player, create them a new PlayerProfile
        if (playerProfile == null) {
            // create and register new PlayerProfile for this player
            playerProfile = new PlayerProfile(player);
            playerProfile.register();
        }

        // update last known username to their current username
        playerProfile.setUsername(player.getName());
    }
}
