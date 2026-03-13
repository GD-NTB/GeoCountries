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
        this.childCommands.put("info", new gcFactionInfo(this, "info", "/gc faction info", "gc.faction.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        // todo: /gc faction list
        // todo: /gc faction apply
        // todo: /gc faction invite
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
