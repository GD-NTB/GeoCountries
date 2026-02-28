package me.rntb.geoCountries.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        PlayerProfile player = PlayerProfile.get(event.getPlayer());

        // prepend country prefix to start of player message
        doPrefixLogic(event, player);

        // confirm or cancel chat response
        doResponseLogic(event, player);
    }

    private void doPrefixLogic(AsyncChatEvent event, PlayerProfile player) {
        Country playerCountry = player.getCitizenship();
        // if not enabled in config, player doesnt have country, country has prefix disabled, or country prefix is null, escape
        if (!ConfigState.countryPrefixEnabled || playerCountry == null || playerCountry.settings.get("prefixenabled").equals("false") || playerCountry.settings.get("prefix").equals("null"))
            return;

        // build prefix and prepend
        ChatUtil.ChatColour chatColour = ChatUtil.ChatColour.WHITE;
        try {
            chatColour = ChatUtil.ChatColour.valueOf(playerCountry.settings.get("prefixcolour"));
        } catch (IllegalArgumentException ignored) { }
        Component prefix = Component.text((ConfigState.countryPrefixFormat + " ")
                                          .formatted(ChatUtil.getChatColourByEnum(chatColour),
                                                     playerCountry.settings.get("prefix")));
        event.renderer((source, sourceDisplayName, message, viewer) ->
                prefix.append(Component.text("§r")).append(sourceDisplayName).append(Component.text(": ")).append(message)
        );
    }

    private void doResponseLogic(AsyncChatEvent event, PlayerProfile player) {
        UUID uuid = player.uuid;

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
