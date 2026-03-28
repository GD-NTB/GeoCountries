package me.rntb.geoCountries.command;

import me.rntb.geoCountries.metadata.PlayerMetadata;
import me.rntb.geoCountries.menu.MenuPage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gcGui extends GeoCommand {

    public gcGui(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Opens the GeoCountries command GUI menu.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        player.closeInventory(); // close any inventory already open

        // open base menu page
        PlayerMetadata.previousPage.put(player.getUniqueId(), GeoCommand.getBaseCommand().getCommandString());
        MenuPage.openMenuPage(GeoCommand.getBaseCommand().getMenuButtons(player), GeoCommand.getBaseCommand(), player);
    }
}
