package me.rntb.geoCountries.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Response;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ResponseChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        PlayerProfile player = PlayerProfile.get(event.getPlayer());

        UUID uuid = player.getUUID();

        // if wasn't waiting for response, escape
        if (!Response.isWaiting(uuid))
            return;

        // cancel player's original message
        event.setCancelled(true);

        Response response = Response.get(uuid);

        // remove sender from waiting list
        Response.stopWaiting(uuid, Response.StopWaitingEvent.PLAYER_SENT_MESSAGE, true);

        // execute
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        response.function.accept(response.sender, message);
    }
}
