package me.rntb.geoCountries.types;

import me.rntb.geoCountries.GeoCountries;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class MenuPage {

    public static final NamespacedKey COMMAND_KEY = new NamespacedKey(GeoCountries.self, "command");
    public static final NamespacedKey ISMENUOPEN_KEY = new NamespacedKey(GeoCountries.self, "isMenuOpen");

    public static Inventory createPage(ItemStack[] buttons, Player player) {
        int buttonCount = buttons.length;

        int rows = 2 + (Math.ceilDiv(buttonCount, 7));
        int slots = 9 * rows;

        Inventory inventory = Bukkit.createInventory(player, slots, Component.text("§8/gc"));

        // 6 commands per row, padded left and right
        int subCommandIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int flatIndex = row*9 + col;

                // set close button
                if (flatIndex == 8) {
                    inventory.setItem(flatIndex, createButton(Material.BARRIER, "§cClose", null, "CLOSE", player));
                    continue;
                }

                // pad top, bottom, left, and right
                if (row == 0 || row == rows-1 || col == 0 || col == 8) {
                    inventory.setItem(flatIndex, createButton(Material.LIME_STAINED_GLASS_PANE, "", null, null, player));
                    continue;
                }

                // put item in inventory
                inventory.setItem(flatIndex, buttons[subCommandIndex]);
                subCommandIndex++;
            }
        }

        return inventory;
    }

    public static ItemStack createButton(Material material, String title, String description, String command, Player player) {
        ItemStack item = ItemStack.of(material);

        item.editMeta(meta -> {
            if (title != null)
                meta.displayName(Component.text(title));
            if (description != null)
                meta.lore(List.of(Component.text("§f" + description)));
            if (command != null)
                meta.getPersistentDataContainer().set(COMMAND_KEY, PersistentDataType.STRING, command);
            // set player head to player's skin
            if (material == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta)
                skullMeta.setOwningPlayer(player);
        });

        return item;
    }
}
