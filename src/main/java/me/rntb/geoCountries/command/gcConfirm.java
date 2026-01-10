package me.rntb.geoCountries.command;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class gcConfirm extends SubCommand {

    public gcConfirm(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Confirms a command or action.";
        this.HelpPage   = """
                          §f/gc confirm§a: Confirms the last command/action you were asked to type §f/gc confirm§a for.
                          """;
    }

    // senders (players/console we are waiting to type /gc confirm), and the method to call on confirm
    // values store the method (BiConsumer<CommandSender, String[]>), the sender (CommandSender) and the args (String[])
    private static final Map<UUID, Triple<BiConsumer<CommandSender, String[]>,
                                          CommandSender,
                                          String[]>> PendingConfirms = new HashMap<>();
    // keeps track of timeout tasks
    private static final Map<UUID, BukkitTask> TimeoutTasks = new HashMap<>();

//    public static BiConsumer<CommandSender, String[]> GetConfirmFunctionOfSender(CommandSender commandSender) {
//        if (IsWaitingForSender(UuidUtil.GetUUIDOfCommandSender(commandSender))) {
//            return PendingConfirms.values().stream().filter(x -> x.getMiddle() == commandSender).findFirst().get().getLeft();
//        }
//        return null;
//    }

    public static Boolean IsWaitingForSender(UUID uuid) {
        return PendingConfirms.containsKey(uuid);
    }

    // add
    public static void WaitForConfirm(UUID uuid, Triple<BiConsumer<CommandSender, String[]>, CommandSender, String[]> methodAfterConfirm) {
        PendingConfirms.put(uuid, methodAfterConfirm);
        ChatUtil.SendPrefixedMessage(methodAfterConfirm.getMiddle(), "§eType §f/gc confirm§e to confirm, else type §f/gc cancel§e to cancel.");

        // timeout after 30 seconds
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(GeoCountries.self, () -> {
                                     StopWaitingForSender(uuid);
                                     ChatUtil.SendPrefixedMessage(Bukkit.getPlayer(uuid), "§cCommand timed out because you didn't confirm after §f30 seconds§c!");
                                 }, 30*20); // 30 seconds * 20 ticks
        // add to timeout tasks dict
        TimeoutTasks.put(uuid, timeoutTask);
    }

    // remove
    public static boolean StopWaitingForSender(UUID uuid) {
        // remove and cancel timeout task
        if (PendingConfirms.remove(uuid) != null) {
            // since entry existed in pending confirms map, it will exist in timeout tasks map
            BukkitTask timeoutTask = TimeoutTasks.get(uuid);
            timeoutTask.cancel();
            TimeoutTasks.remove(uuid);

            ChatUtil.SendPrefixedLogMessage("Stopped waiting for " + uuid.toString() + "to do /gc confirm.");
            return true;
        }

        return false;
    }

    @Override
    public void doCommand(CommandSender sender, String[] args) {
        // if console, uuid=0000..., else get player uuid
        UUID uuid = UuidUtil.GetUUIDOfCommandSender(sender);

        // if sender not being waited on, escape
        if (!IsWaitingForSender(uuid)) {
            ChatUtil.SendPrefixedMessage(sender, "§cNo command was waiting to be confirmed.");
            return;
        }

        // get method, sender and args from PendingConfirms
        Triple<BiConsumer<CommandSender, String[]>, CommandSender, String[]> methodAndArgs = PendingConfirms.get(uuid);
        BiConsumer<CommandSender, String[]> _method = methodAndArgs.getLeft();
        CommandSender _sender = methodAndArgs.getMiddle();
        String[] _args = methodAndArgs.getRight();

        // execute
        _method.accept(_sender, _args);

        // remove sender from waiting list
        StopWaitingForSender(uuid);
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
