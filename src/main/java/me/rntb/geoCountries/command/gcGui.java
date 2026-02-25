package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.MenuPage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class gcGui extends GeoCommand {

    public gcGui(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Opens the GeoCountries visual GUI menu.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        player.openInventory(MenuPage.createPage(GeoCommand.baseCommand.getMenuButtons(player), player));
        player.getPersistentDataContainer().set(MenuPage.ISMENUOPEN_KEY, PersistentDataType.BOOLEAN, true); // set menu flag to open
    }
}
