package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFaction extends GeoCommand {

    public gcFaction(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Manages and views info about factions.");
        addChild(new gcFactionCreate("create", "gc.faction.create", ItemStack.of(Material.NETHER_STAR)));
        addChild(new gcFactionRename("rename", "gc.faction.rename", ItemStack.of(Material.NAME_TAG)));
        addChild(new gcFactionInvite("invite", "gc.faction.invite", ItemStack.of(Material.TOTEM_OF_UNDYING)));
        addChild(new gcFactionUninvite("uninvite", "gc.faction.uninvite", ItemStack.of(Material.CARROT_ON_A_STICK)));
        addChild(new gcFactionMembers("members", "gc.faction.members", ItemStack.of(Material.PLAYER_HEAD)));
        addChild(new gcFactionInfo("info", "gc.faction.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        addChild(new gcFactionList("list", "gc.faction.list", ItemStack.of(Material.MAP)));
        addChild(new gcFactionReceived("received", "gc.faction.received", ItemStack.of(Material.CHEST)));
        addChild(new gcFactionSent("sent", "gc.faction.sent", ItemStack.of(Material.BOOK)));
        addChild(new gcFactionAccept("accept", "gc.faction.accept", null));
        addChild(new gcFactionDecline("decline", "gc.faction.decline", null));
        addChild(new gcFactionKick("kick", "gc.faction.kick", ItemStack.of(Material.GRINDSTONE)));
        addChild(new gcFactionDisband("disband", "gc.faction.disband", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcFactionTransfer("transfer", "gc.faction.transfer", ItemStack.of(Material.ENDER_PEARL)));
        addChild(new gcFactionLeave("leave", "gc.faction.leave", ItemStack.of(Material.DARK_OAK_DOOR)));
        // todo: /gc faction settings
        addAlias("f");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            getChild("info").onCommandEntered(sender, args);
            return;
        }
        doChildCommand(sender, args);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).hasCitizenship();
    }
}
