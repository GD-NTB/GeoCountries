package me.rntb.geoCountries.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.rntb.geoCountries.types.Response;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

    public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // if wasn't waiting for response, escape
        if (!Response.isWaiting(uuid))
            return;

        // cancel player's original message
        event.setCancelled(true);

        // execute
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Response response = Response.get(uuid);
        response.function.accept(response.sender, message);

        // remove sender from waiting list
        Response.stopWaiting(uuid, Response.StopWaitingEvent.PLAYER_SENT_MESSAGE, true);
    }
}
