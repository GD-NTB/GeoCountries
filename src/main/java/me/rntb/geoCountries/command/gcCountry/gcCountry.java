package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gcCitizenship.gcCitizenshipRenounce;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcCountry extends GeoCommand {

    public gcCountry(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Manages and views info about countries.";
        addChild(new gcCountryCreate("create", "gc.country.create", ItemStack.of(Material.NETHER_STAR)));
        addChild(new gcCountryRename("rename", "gc.country.rename", ItemStack.of(Material.NAME_TAG)));
        addChild(new gcCountryCitizens("citizens", "gc.country.citizens", ItemStack.of(Material.PLAYER_HEAD)));
        addChild(new gcCountryInfo("info", "gc.country.info", ItemStack.of(Material.JUNGLE_HANGING_SIGN)));
        addChild(new gcCountryList("list", "gc.country.list", ItemStack.of(Material.MAP)));
        addChild(new gcCountrySettings("settings", "gc.country.settings", ItemStack.of(Material.WRITABLE_BOOK)));
        addChild(new gcCountryDissolve("dissolve", "gc.country.dissolve", ItemStack.of(Material.FLINT_AND_STEEL)));
        addChild(new gcCountryTransfer("transfer", "gc.country.transfer", ItemStack.of(Material.ENDER_PEARL)));
        addChild(new gcCitizenshipRenounce("leave", "gc.citizenship.renounce", ItemStack.of(Material.DARK_OAK_DOOR)));
        addAlias("c");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            childLookup.get("info").onCommandEntered(sender, args);
            return;
        }
        doChildCommand(sender, args);
    }
}
