package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenshipRenounce;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcCountry extends GeoCommand {

    public gcCountry(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages and views info about countries.";
        this.childCommands.put("create", new gcCountryCreate(this, "create", "/gc country create", "gc.country.create", ItemStack.of(Material.NETHER_STAR)));
        this.childCommands.put("rename", new gcCountryRename(this, "rename", "/gc country rename", "gc.country.rename", ItemStack.of(Material.NAME_TAG)));
        this.childCommands.put("citizens", new gcCountryCitizens(this, "citizens", "/gc country citizens", "gc.country.citizens", ItemStack.of(Material.PLAYER_HEAD)));
        this.childCommands.put("info", new gcCountryInfo(this, "info", "/gc country info", "gc.country.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        this.childCommands.put("list", new gcCountryList(this, "list", "/gc country list", "gc.country.list", ItemStack.of(Material.MAP)));
        this.childCommands.put("settings", new gcCountrySettings(this, "settings", "/gc country settings", "gc.country.settings", ItemStack.of(Material.WRITABLE_BOOK)));
        this.childCommands.put("dissolve", new gcCountryDissolve(this, "dissolve", "/gc country dissolve", "gc.country.dissolve", ItemStack.of(Material.FLINT_AND_STEEL)));
        this.childCommands.put("transfer", new gcCountryTransfer(this, "transfer", "/gc country transfer", "gc.country.transfer", ItemStack.of(Material.ENDER_PEARL)));
        this.childCommands.put("leave", new gcCitizenshipRenounce(this, "leave", "/gc citizenship renounce", "gc.citizenship.renounce", ItemStack.of(Material.DARK_OAK_DOOR)));
        this.childCommandsAliases.put("members", "citizens");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childCommands.get("info").onCommandEntered(sender, args);
            return;
        }
        findAndExecuteChildCommand(sender, args);
    }
}
