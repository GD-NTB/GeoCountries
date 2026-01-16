package me.rntb.geoCountries.types;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

// when using a response, make sure whatever uses it gets cancelled by updating stopWaiting method
public class Response {

    private static final Map<UUID, Response> pendingResponses = new HashMap<>();
    private static final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    public static boolean isWaiting(UUID uuid) {
        return pendingResponses.containsKey(uuid);
    }

    public static Response get(UUID uuid) {
        return pendingResponses.get(uuid);
    }

    public static void startWaiting(UUID uuid, Response response, boolean sendMessage) {
        pendingResponses.put(uuid, response);

        // timeout after x seconds
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(GeoCountries.self,
                                                                    () -> stopWaiting(uuid, StopWaitingEvent.TIMED_OUT, true),
                                                                    response.timeoutAfterSeconds *20); // 20 ticks = 1 second

        // add to timeout tasks dict
        timeoutTasks.put(uuid, timeoutTask);

        // send message
        if (sendMessage && response.startWaitingMessage != null)
            ChatUtil.sendPrefixedMessage(response.sender, response.startWaitingMessage);
    }

    public enum StopWaitingEvent {
        PLAYER_SENT_MESSAGE,
        CANCELLED,
        TIMED_OUT
    }
    public static void stopWaiting(UUID uuid, StopWaitingEvent stopWaitingEvent, boolean sendMessage) {
        Response response = pendingResponses.get(uuid);
        if (response == null)
            return;

        pendingResponses.remove(uuid);

        timeoutTasks.get(uuid).cancel();
        timeoutTasks.remove(uuid);

        // cancel whatever was going to use the response
        if (stopWaitingEvent != StopWaitingEvent.PLAYER_SENT_MESSAGE) {
            CitizenshipApplication.cancel(CitizenshipApplication.openByApplicant.get(uuid), true);
        }

        // send appropriate message
        Player player = Bukkit.getPlayer(uuid);
        if (sendMessage && player != null) {
            switch (stopWaitingEvent) {
                case StopWaitingEvent.PLAYER_SENT_MESSAGE:
                    if (response.playerSentMessageMessage != null)
                        ChatUtil.sendPrefixedMessage(player, response.playerSentMessageMessage);
                    break;
                case CANCELLED:
                    break;
                case StopWaitingEvent.TIMED_OUT:
                    if (response.timeoutMessage != null)
                        ChatUtil.sendPrefixedMessage(player, response.timeoutMessage);
                    break;

            }
        }

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Stopped waiting for " + uuid + " to type in chat.");
    }

    // ---

    public BiConsumer<CommandSender, String> function; // on response function, String=chat message
    public CommandSender sender; // sender argument for Function
    public long timeoutAfterSeconds = 30;
    public String startWaitingMessage;
    public String playerSentMessageMessage;
    public String timeoutMessage;

    public Response(BiConsumer<CommandSender, String> function, CommandSender sender) {
        this.function = function;
        this.sender = sender;

        this.startWaitingMessage = "§6Type in chat, or do §f/gc cancel§6 to cancel.";
        this.playerSentMessageMessage = null;
        this.timeoutMessage = "§cTimed out because you didn't type anything in chat after §f%d second%s§c!"
                              .formatted(this.timeoutAfterSeconds, StringUtil.LeadingS(this.timeoutAfterSeconds));
    }
}

