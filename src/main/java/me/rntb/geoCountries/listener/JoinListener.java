package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // register new player in player list
        if (PlayerData.PlayerDataByUUID.getOrDefault(player.getUniqueId(), null) == null) {
            // create and register new PlayerData for this player
            PlayerData newPlayerData = new PlayerData(player);
            PlayerData.AddNew(newPlayerData);
        }
    }
}
