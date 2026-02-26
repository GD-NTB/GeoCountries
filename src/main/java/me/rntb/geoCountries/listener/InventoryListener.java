package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.types.MenuPage;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.Bukkit;
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
        boolean isMenuOpen = player.getPersistentDataContainer().has(MenuPage.ISMENUOPEN_KEY, PersistentDataType.BOOLEAN);
        if (!isMenuOpen)
            return;

        event.setCancelled(true);
        // get command of item clicked
        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked == null)
            return;

        String commandString = itemClicked.getPersistentDataContainer().get(MenuPage.COMMAND_KEY, PersistentDataType.STRING);
        if (commandString == null)
            return;

        // find command
        if (commandString.equals("GUI_CLOSE"))
            player.closeInventory();
        else {
            GeoCommand command = GeoCommand.getByCommandString.get(commandString);
            if (command == null)
                return;

            // if this command has no menu buttons, execute, else open its page
            ItemStack[] commandButtons = command.getMenuButtons(player);
            if (commandButtons == null) {
                command.onCommandEntered(player, new String[] { });
                // close page after click
                // todo: config option for this, default = false
                Bukkit.getScheduler().runTask(GeoCountries.self, () -> player.closeInventory()); // 1 tick delay
                player.getPersistentDataContainer().remove(MenuPage.ISMENUOPEN_KEY); // set menu flag to closed
            }
            else
                MenuPage.openMenuPage(player, command.command, command.getMenuButtons(player));
        }

        // play click sound
        SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // no matter what menu, set menu flag to closed
        event.getPlayer().getPersistentDataContainer().remove(MenuPage.ISMENUOPEN_KEY);
    }
}
