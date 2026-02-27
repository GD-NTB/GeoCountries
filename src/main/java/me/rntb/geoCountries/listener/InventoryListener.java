package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.type.MenuPage;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // if this wasn't our menu, escape
        boolean isMenuOpen = MenuPage.playerIsMenuOpen.get(player.getUniqueId());
        if (!isMenuOpen)
            return;

        event.setCancelled(true);
        // get command of item clicked
        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked == null)
            return;

        String commandString = itemClicked.getPersistentDataContainer().get(MenuPage.ITEM_COMMAND_KEY, PersistentDataType.STRING);
        if (commandString == null)
            return;

        // these are hardcoded "command" commands... yeah
        // GUI_CLOSE
        if (commandString.equals("GUI_CLOSE")) {
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
            MenuPage.openMenuPage(command.getMenuButtons(player), command.command, player, commandString.equals(GeoCommand.baseCommand.command));
            MenuPage.playerPreviousPage.put(player.getUniqueId(), command.command);
        }

        // play click sound
        SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // no matter what menu, set menu flag to closed
        MenuPage.playerIsMenuOpen.put(player.getUniqueId(), false);
    }
}
