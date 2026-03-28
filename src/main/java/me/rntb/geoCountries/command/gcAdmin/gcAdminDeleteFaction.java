package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcAdminDeleteFaction extends GeoCommand {

    public gcAdminDeleteFaction(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Deletes a faction from the server.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the name of the faction you want to delete!");
            return;
        }

        String factionName = String.join(" ", args).trim();

        Faction faction = Faction.get(factionName);
        if (faction == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cFaction §f" + factionName + "§c does not exist!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                          sender,
                                          new String[] { factionName }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        Faction faction = Faction.get(args[0]);
        faction.deregister();

        ChatUtil.sendPrefixedMessage(sender, "§aDeleted faction!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }
}
