package me.rntb.geoCountries.listener;

import me.rntb.geoCountries.GeoCountries;
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

        String command = itemClicked.getPersistentDataContainer().get(MenuPage.COMMAND_KEY, PersistentDataType.STRING);
        if (command == null)
            return;

        // execute command
        // todo: open into other pages
        if (!command.equals("CLOSE"))
            player.performCommand(command.substring(1));

        // close page after click
        Bukkit.getScheduler().runTask(GeoCountries.self, () -> player.closeInventory()); // needs to have 1 tick delay
        player.getPersistentDataContainer().remove(MenuPage.ISMENUOPEN_KEY); // set menu flag to closed

        // play click sound
        SoundUtil.playSound(player, SoundUtil.SoundEffect.MENU_CLICK);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // no matter what menu, set menu flag to closed
        event.getPlayer().getPersistentDataContainer().remove(MenuPage.ISMENUOPEN_KEY);
    }
}
