package me.rntb.geoCountries.type;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Response {

    private static final List<Consumer<UUID>> onFailMethods = new ArrayList<>(); // executed when response ends without a player chat message
    public static List<Consumer<UUID>> getOnFailMethods() {
        return onFailMethods;
    }

    private static final Map<UUID, Response> pendingResponses = new HashMap<>();
    private static final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    public static boolean isWaiting(UUID uuid) {
        return pendingResponses.containsKey(uuid);
    }

    public static Response get(UUID uuid) {
        return pendingResponses.get(uuid);
    }

    public static void startWaiting(UUID uuid, Response response, boolean sendMessage) {
        // stop waiting if we were already waiting
        stopWaiting(uuid, StopWaitingEvent.CANCELLED, true);

        pendingResponses.put(uuid, response);

        // timeout after x seconds
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(GeoCountries.self,
                                                                    () -> stopWaiting(uuid, StopWaitingEvent.TIMED_OUT, true),
                                                                    response.timeoutAfterSeconds * 20); // 20 ticks = 1 second

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

        // send appropriate message
        Player player = Bukkit.getPlayer(uuid);
        if (sendMessage) {
            switch (stopWaitingEvent) {
                case PLAYER_SENT_MESSAGE:
                    if (response.playerSentMessageMessage != null)
                        ChatUtil.sendPrefixedMessage(player, response.playerSentMessageMessage);
                    break;
                case CANCELLED:
                    if (response.cancelMessage != null)
                        ChatUtil.sendPrefixedMessage(player, response.cancelMessage);
                    break;
                case TIMED_OUT:
                    if (response.timeoutMessage != null)
                        ChatUtil.sendPrefixedMessage(player, response.timeoutMessage);
                    break;
            }
        }

        // cancel whatever was going to use the response
        if (stopWaitingEvent != StopWaitingEvent.PLAYER_SENT_MESSAGE && !onFailMethods.isEmpty()) {
            for (Consumer<UUID> method : onFailMethods) {
                method.accept(uuid);
            }
        }

        if (ConfigState.debugLogging && player != null)
            ChatUtil.sendPrefixedLogMessage("Stopped pending Response from " + player.getName() + " (" + stopWaitingEvent.name() + ").");
    }

    // ---

    private final BiConsumer<CommandSender, String> onSuccessMethod; // on response method, String=chat message
    public  BiConsumer<CommandSender, String> getOnSuccessMethod() {
        return onSuccessMethod;
    }

    private final CommandSender sender; // sender argument for method
    public CommandSender getSender() {
        return sender;
    }

    private long timeoutAfterSeconds = 30;
    public long getTimeoutAfterSeconds() {
        return timeoutAfterSeconds;
    }
    public void setTimeoutAfterSeconds(long value) {
        timeoutAfterSeconds = value;
    }

    private String startWaitingMessage;
    public String getStartWaitingMessage() {
        return startWaitingMessage;
    }
    public void setStartWaitingMessage(String value) {
        startWaitingMessage = value;
    }

    private String playerSentMessageMessage;
    public String getPlayerSentMessageMessage() {
        return playerSentMessageMessage;
    }
    public void setPlayerSentMessageMessage(String value) {
        playerSentMessageMessage = value;
    }

    private String cancelMessage;
    public String getCancelMessage() {
        return cancelMessage;
    }
    public void setCancelMessage(String value) {
        cancelMessage = value;
    }

    private String timeoutMessage;
    public String getTimeoutMessage() {
        return timeoutMessage;
    }
    public void setTimeoutMessage(String value) {
        timeoutMessage = value;
    }

    public Response(BiConsumer<CommandSender, String> onSuccessMethod, CommandSender sender) {
        this.onSuccessMethod = onSuccessMethod;
        this.sender = sender;

        this.startWaitingMessage = "§6Type in chat, or do §f/gc cancel§6 to cancel.";
        this.playerSentMessageMessage = null;
        this.cancelMessage = "§aCancelled!";
        this.timeoutMessage = "§cTimed out because you didn't type anything in chat after §f%d second%s§c!"
                              .formatted(timeoutAfterSeconds, StringUtil.leadingS(timeoutAfterSeconds));
    }
}

