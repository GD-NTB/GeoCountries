package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.types.Confirmation;
import me.rntb.geoCountries.types.Response;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // stop pending /gc confirm
        Confirmation.stopWaiting(uuid, Confirmation.StopWaitingEvent.CANCELLED, false);
        Response.stopWaiting(uuid, Response.StopWaitingEvent.CANCELLED, false);
    }
}
