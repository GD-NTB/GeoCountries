package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcFaction extends GeoCommand {

    public gcFaction(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages and views info about factions.";
        this.childCommands.put("create", new gcFactionCreate(this, "create", "/gc faction create", "gc.faction.create", ItemStack.of(Material.NETHER_STAR)));
        this.childCommands.put("rename", new gcFactionRename(this, "rename", "/gc faction rename", "gc.faction.rename", ItemStack.of(Material.NAME_TAG)));
        this.childCommands.put("invite", new gcFactionInvite(this, "invite", "/gc faction invite", "gc.faction.invite", ItemStack.of(Material.TOTEM_OF_UNDYING)));
        this.childCommands.put("uninvite", new gcFactionUninvite(this, "uninvite", "/gc faction uninvite", "gc.faction.uninvite", ItemStack.of(Material.CARROT_ON_A_STICK)));
        this.childCommands.put("members", new gcFactionMembers(this, "members", "/gc faction members", "gc.faction.members", ItemStack.of(Material.PLAYER_HEAD)));
        this.childCommands.put("info", new gcFactionInfo(this, "info", "/gc faction info", "gc.faction.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        this.childCommands.put("list", new gcFactionList(this, "list", "/gc faction list", "gc.faction.list", ItemStack.of(Material.MAP)));
        this.childCommands.put("received", new gcFactionReceived(this, "received", "/gc faction received", "gc.faction.received", ItemStack.of(Material.CHEST)));
        this.childCommands.put("sent", new gcFactionSent(this, "sent", "/gc faction sent", "gc.faction.sent", ItemStack.of(Material.BOOK)));
        this.childCommands.put("accept", new gcFactionAccept(this, "accept", "/gc faction accept", "gc.faction.accept", null));
        this.childCommands.put("decline", new gcFactionDecline(this, "decline", "/gc faction decline", "gc.faction.decline", null));
        this.childCommands.put("disband", new gcFactionDisband(this, "disband", "/gc faction disband", "gc.faction.disband", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("leave", new gcFactionLeave(this, "leave", "/gc faction leave", "gc.faction.leave", ItemStack.of(Material.OAK_DOOR)));
        // todo: /gc faction settings
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).hasCitizenship();
    }
}
