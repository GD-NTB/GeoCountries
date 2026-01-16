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

// when using a confirmaton, make sure whatever uses it gets cancelled by updating stopWaiting method
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
        pendingConfirmations.put(uuid, confirmation);

        // timeout after x seconds
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(GeoCountries.self,
                                                                    () -> stopWaiting(uuid, StopWaitingEvent.TIMED_OUT, true),
                                                                    confirmation.timeoutAfterSeconds *20); // 20 ticks = 1 second

        // send message
        if (sendMessage && confirmation.startMessage != null)
            ChatUtil.sendPrefixedMessage(confirmation.sender, confirmation.startMessage);

        // add to timeout tasks dict
        timeoutTasks.put(uuid, timeoutTask);
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
        if (stopWaitingEvent != StopWaitingEvent.CONFIRMED) {
            CitizenshipApplication.cancel(CitizenshipApplication.openByApplicant.get(uuid), true);
        }

        // send appropriate message
        Player player = Bukkit.getPlayer(uuid);
        if (sendMessage && player != null) {
            switch (stopWaitingEvent) {
                case StopWaitingEvent.CONFIRMED:
                    if (confirmation.confirmMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.confirmMessage);
                    break;
                case StopWaitingEvent.CANCELLED:
                    if (confirmation.cancelMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.cancelMessage);
                    break;
                case StopWaitingEvent.TIMED_OUT:
                    if (confirmation.timeoutMessage != null)
                        ChatUtil.sendPrefixedMessage(player, confirmation.timeoutMessage);
                    break;
            }
        }

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Stopped waiting for " + uuid + " to do /gc confirm.");
    }

    // ---

    public BiConsumer<CommandSender, String[]> function; // on confirmation function
    public CommandSender sender; // sender argument for function
    public String[] args; // string args argument for function
    public long timeoutAfterSeconds = 20;
    public String startMessage;
    public String confirmMessage;
    public String cancelMessage;
    public String timeoutMessage;

    public Confirmation(BiConsumer<CommandSender, String[]> function, CommandSender sender, String[] args) {
        this.function = function;
        this.sender = sender;
        this.args = args;

        this.startMessage = "§6Do §f/gc confirm§6 to confirm, or do §f/gc cancel§6 to cancel.";
        this.confirmMessage = "§6Confirmed!";
        this.cancelMessage = "§6Cancelled the action!";
        this.timeoutMessage = "§cTimed out because you didn't confirm after §f%d second%s§c!"
                              .formatted(this.timeoutAfterSeconds, StringUtil.LeadingS(this.timeoutAfterSeconds));
    }
}
