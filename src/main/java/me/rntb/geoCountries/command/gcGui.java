package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.MenuPage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gcGui extends GeoCommand {

    public gcGui(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Opens the GeoCountries visual GUI menu.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        player.closeInventory(); // close any inventory already open

        // open base menu page
        MenuPage.openMenuPage(player, GeoCommand.baseCommand.command, GeoCommand.baseCommand.getMenuButtons(player));
    }
}
