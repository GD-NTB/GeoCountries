package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.metadata.ItemMetadata;
import me.rntb.geoCountries.metadata.PlayerMetadata;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // if this wasn't our menu, escape
        boolean isMenuOpen = PlayerMetadata.isMenuOpen.get(player.getUniqueId());
        if (!isMenuOpen)
            return;

        event.setCancelled(true);
        // get command of item clicked
        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked == null)
            return;

        String commandString = ItemMetadata.getItemCommand(itemClicked);
        if (commandString == null)
            return;

        // GUI_CLOSE
        if (commandString.equals("GUI_CLOSE")) {
            player.closeInventory();
            SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
            return;
        }

        // if starts with GUI_RUN_COMMAND:, implicitly execute the rest of the string
        if (commandString.startsWith("GUI_RUN_COMMAND:")) {
            String command = commandString.substring(16);
            player.performCommand(command);
            player.closeInventory();
            SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
            return;
        }

        GeoCommand command = GeoCommand.getByCommandString.get(commandString);

        if (command == null)
            return;

        // if this command has no menu buttons, execute, else open its page
        ItemStack[] commandButtons = command.getMenuButtons(player);
        if (commandButtons == null) {
            MenuPage.closeMenuPage(player);
            command.onCommandEntered(player, new String[] { });
        }
        else {
            MenuPage.openMenuPage(command.getMenuButtons(player), command, player, commandString.equals(GeoCommand.baseCommand.command));
            PlayerMetadata.previousPage.put(player.getUniqueId(), command.command);
        }

        // play click sound
        SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // no matter what menu, set menu flag to closed
        PlayerMetadata.isMenuOpen.put(player.getUniqueId(), false);
    }
}
