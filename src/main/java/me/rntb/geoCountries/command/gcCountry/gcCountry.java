package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcCountry extends GeoCommand {

    public gcCountry(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages, edits, and views info about countries.";
        this.childCommands = new LinkedHashMap<>() {{
            put("create", new gcCountryCreate("create", "/gc country create", "gc.country.create", ItemStack.of(Material.NETHER_STAR)));
            put("rename", new gcCountryRename("rename", "/gc country rename", "gc.country.rename", ItemStack.of(Material.NAME_TAG)));
            put("citizens", new gcCountryCitizens("citizens", "/gc country citizens", "gc.country.citizens", ItemStack.of(Material.TOTEM_OF_UNDYING)));
            put("info", new gcCountryInfo("info", "/gc country info", "gc.country.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
            put("list", new gcCountryList("list", "/gc country list", "gc.country.list", ItemStack.of(Material.MAP)));
            put("dissolve", new gcCountryDissolve("dissolve", "/gc country dissolve", "gc.country.dissolve", ItemStack.of(Material.FLINT_AND_STEEL)));
            put("settings", new gcCountrySettings("settings", "/gc country settings", "gc.country.settings", ItemStack.of(Material.WRITABLE_BOOK)));
        }};
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
