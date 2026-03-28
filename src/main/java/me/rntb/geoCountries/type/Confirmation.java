package me.rntb.geoCountries.type;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
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

public class Confirmation {

    private static final Map<UUID, Confirmation> pendingConfirmations = new HashMap<>();
    private static final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    public static boolean isWaiting(UUID uuid) {
        return pendingConfirmations.containsKey(uuid);
    }

    public static Confirmation get(UUID uuid) {
        return pendingConfirmations.get(uuid);
    }

    public static void startWaiting(UUID uuid, Confirmation confirmation, boolean sendMessage) {
        // if disabled in config, just execute method
        if (!ConfigState.enableGcConfirm) {
            confirmation.function.accept(confirmation.sender, confirmation.args);
            return;
        }

        stopWaiting(uuid, StopWaitingEvent.CANCELLED, true);

        pendingConfirmations.put(uuid, confirmation);

        // timeout after x seconds
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(GeoCountries.self,
                                                                    () -> stopWaiting(uuid, StopWaitingEvent.TIMED_OUT, true),
                                                                    confirmation.timeoutAfterSeconds *20); // 20 ticks = 1 second

        // add to timeout tasks dict
        timeoutTasks.put(uuid, timeoutTask);

        // send message
        if (sendMessage && confirmation.startMessage != null)
            ChatUtil.sendPrefixedMessage(confirmation.sender, confirmation.startMessage);
    }

    public enum StopWaitingEvent {
        CONFIRMED,
        CANCELLED,
        TIMED_OUT
    }
    public static void stopWaiting(UUID uuid, StopWaitingEvent stopWaitingEvent, boolean sendMessage) {
        Confirmation confirmation = pendingConfirmations.get(uuid);
        if (confirmation == null)
            return;

        pendingConfirmations.remove(uuid);

        timeoutTasks.get(uuid).cancel();
        timeoutTasks.remove(uuid);

        // cancel whatever was going to use the confirmation
        // ---

        // send appropriate message
        Player player = Bukkit.getPlayer(uuid);
        if (sendMessage && player != null) {
            switch (stopWaitingEvent) {
                case CONFIRMED:
                    if (confirmation.confirmMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.confirmMessage);
                    break;
                case CANCELLED:
                    if (confirmation.cancelMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.cancelMessage);
                    break;
                case TIMED_OUT:
                    if (confirmation.timeoutMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.timeoutMessage);
                    break;
            }
        }

        if (ConfigState.debugLogging && player != null)
            ChatUtil.sendPrefixedLogMessage("Stopped pending Confirmation from " + player.getName() + " (" + stopWaitingEvent.name() + ").");
    }

    // ---

    private final BiConsumer<CommandSender, String[]> function; // on confirmation function
    public BiConsumer<CommandSender, String[]> getFunction() {
        return function;
    }

    private final CommandSender sender; // sender argument for function
    public CommandSender getSender() {
        return sender;
    }

    private final String[] args; // string args argument for function
    public String[] getArgs() {
        return args;
    }

    private final long timeoutAfterSeconds = 20;
    public long getTimeoutAfterSeconds() {
        return timeoutAfterSeconds;
    }

    private String startMessage;
    public String getStartMessage() {
        return startMessage;
    }
    public void setStartMessage(String value) {
        startMessage = value;
    }

    private String confirmMessage;
    public String getConfirmMessage() {
        return confirmMessage;
    }
    public void setConfirmMessage(String value) {
        confirmMessage = value;
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

    public Confirmation(BiConsumer<CommandSender, String[]> function, CommandSender sender, String[] args) {
        this.function = function;
        this.sender = sender;
        this.args = args;

        this.startMessage = "§6Do §f/gc confirm§6 to confirm, or do §f/gc cancel§6 to cancel.";
        this.confirmMessage = "§aConfirmed!";
        this.cancelMessage = "§aCancelled!";
        this.timeoutMessage = "§cTimed out because you didn't confirm after §f%d second%s§c!"
                              .formatted(timeoutAfterSeconds, StringUtil.leadingS(timeoutAfterSeconds));
    }
}
