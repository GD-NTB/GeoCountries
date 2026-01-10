package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.command.gcConfirm;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        // stop pending /gc confirm
        gcConfirm.StopWaitingForSender(player.getUniqueId());
    }
}
