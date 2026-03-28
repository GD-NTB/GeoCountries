package me.rntb.geoCountries.command;

import me.rntb.geoCountries.command.gcAdmin.gcAdmin;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenship;
import me.rntb.geoCountries.command.gcClaim.gcClaim;
import me.rntb.geoCountries.command.gcConfig.gcConfig;
import me.rntb.geoCountries.command.gcCountry.gcCountry;
import me.rntb.geoCountries.command.gcDebug.gcDebug;
import me.rntb.geoCountries.command.gcFaction.gcFaction;
import me.rntb.geoCountries.command.gcPlayer.gcPlayer;
import me.rntb.geoCountries.command.gcPurge.gcPurge;
import me.rntb.geoCountries.command.gcUnclaim.gcUnclaim;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class gc extends GeoCommand implements TabExecutor {

    public gc(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("The base command for GeoCountries. Opens the command GUI menu.");
        addChild(new gcPlayer("player", "gc.player", ItemStack.of(Material.DEBUG_STICK))); // debug stick -> skull of player
        addChild(new gcCitizenship("citizenship", "gc.citizenship", ItemStack.of(Material.WRITABLE_BOOK)));
        addChild(new gcCountry("country", "gc.country", ItemStack.of(Material.FILLED_MAP)));
        addChild(new gcClaim("claim", "gc.claim", ItemStack.of(Material.GOLDEN_SHOVEL)));
        addChild(new gcUnclaim("unclaim", "gc.claim.unclaim", null));
        addChild(new gcFaction("faction", "gc.faction", ItemStack.of(Material.DIAMOND_CHESTPLATE)));
        addChild(new gcHelp("help", "gc.help", ItemStack.of(Material.KNOWLEDGE_BOOK)));
        addChild(new gcAdmin("admin", "gc.admin", ItemStack.of(Material.DIAMOND_BLOCK)));
        addChild(new gcDebug("debug", "gc.debug", ItemStack.of(Material.REDSTONE)));
        addChild(new gcConfig("config", "gc.config", ItemStack.of(Material.BOOK)));
        addChild(new gcPurge("purge", "gc.purge", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcDump("dump", "gc.dump", ItemStack.of(Material.BAKED_POTATO)));
        addChild(new gcSave("save", "gc.save", ItemStack.of(Material.CAMPFIRE)));
        addChild(new gcLoad("load", "gc.load", ItemStack.of(Material.CARROT_ON_A_STICK)));
        addChild(new gcGui("gui", "gc.gui", null));
        addChild(new gcConfirm("confirm", "gc.confirm", null));
        addChild(new gcCancel("cancel", "gc.cancel", null));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)  {
        if (!sender.hasPermission(getPermission())) {
            ChatUtil.sendNoPermissionMessage(sender, "/" + getCommandString(), getName());
            return true;
        }

        if (args.length == 0)
            getChild("gui").onCommandEntered(sender, new String[0]);
        else
            onCommandArgs(sender, args);

        return true;
    }

    private void onCommandArgs(CommandSender sender, String[] args) {
        // find childCommand
        String childCommandName = args[0];
        GeoCommand childCommand = getChild(childCommandName);
        if (childCommand == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cThe command §f%s %s§c doesn't exist!"
                                                 .formatted("/" + getCommandString(), childCommandName));
            return;
        }

        // if we are waiting for sender to confirm a command, but they sent a different command, cancel waiting
        UUID senderUuid = UuidUtil.getUUIDOfCommandSender(sender);
        if (Confirmation.isWaiting(senderUuid) && (!(args.length == 1 && (args[0].equals("confirm") || args[0].equals("cancel")))))
            Confirmation.stopWaiting(senderUuid, Confirmation.StopWaitingEvent.CANCELLED, true);

        // get subargs (the [...] in /gc [childCommand] [...])
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        // do perms and console check then childCommand.onCommand
        childCommand.onCommandEntered(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return onTabComplete(sender, args);
    }
}
