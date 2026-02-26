package me.rntb.geoCountries.types;

import me.rntb.geoCountries.GeoCountries;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

// todo: move this somewhere out of types package
public class MenuPage {

    // item metadata
    public static final NamespacedKey COMMAND_KEY = new NamespacedKey(GeoCountries.self, "command");
    // player metadata
    public static final NamespacedKey ISMENUOPEN_KEY = new NamespacedKey(GeoCountries.self, "isMenuOpen");
    public static final NamespacedKey TITLE_KEY = new NamespacedKey(GeoCountries.self, "title");

    public static Inventory createPage(ItemStack[] buttons, Player player) {
        int buttonCount = buttons.length;

        int rows = 2 + (Math.ceilDiv(buttonCount, 7));
        int slots = 9 * rows;

        String title = player.getPersistentDataContainer().get(MenuPage.TITLE_KEY, PersistentDataType.STRING);
        Inventory inventory = Bukkit.createInventory(player, slots, Component.text("§8" + title));

        // 6 commands per row, padded left and right
        int childCommandIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int flatIndex = row*9 + col;

                // set close button
                if (flatIndex == 8) {
                    inventory.setItem(flatIndex, createButton(Material.BARRIER, "§cClose", null, "GUI_CLOSE", player));
                    continue;
                }

                // bottom row
                if (row == rows-1) {
                    // set confirm button
                    if (col == 3)
                        inventory.setItem(flatIndex, createButton(Material.LIME_WOOL, "§a/gc confirm", "Confirms a pending command/action.", "/gc confirm", player));
                    // set cancel button
                    else if (col == 5)
                        inventory.setItem(flatIndex, createButton(Material.RED_WOOL, "§c/gc cancel", "Cancels a pending command/action. ", "/gc cancel", player));
                    // else pad bottom
                    else
                        inventory.setItem(flatIndex, getPaddingButton(player));
                    continue;
                }

                // pad top, left, right
                if (row == 0 || col == 0 || col == 8) {
                    inventory.setItem(flatIndex, getPaddingButton(player));
                    continue;
                }

                // put item in inventory
                if (childCommandIndex >= buttonCount)
                    continue;
                inventory.setItem(flatIndex, buttons[childCommandIndex]);
                childCommandIndex++;
            }
        }

        return inventory;
    }

    public static ItemStack getPaddingButton(Player player) {
        return createButton(Material.LIME_STAINED_GLASS_PANE, null, null, null, player);
    }

    public static ItemStack createButton(Material material, String name, String description, String command, Player player) {
        ItemStack item = ItemStack.of(material);

        item.editMeta(meta -> {
            if (name != null)
                meta.displayName(Component.text(name));
            if (description != null)
                meta.lore(List.of(Component.text("§f" + description)));
            if (command != null)
                meta.getPersistentDataContainer().set(COMMAND_KEY, PersistentDataType.STRING, command);

            // hide item hover tooltip shite
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.setHideTooltip(false);

            // set player head to player's skin
            if (material == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta)
                skullMeta.setOwningPlayer(player);
        });

        return item;
    }

    public static void openMenuPage(Player player, String title, ItemStack[] buttons) {
        PersistentDataContainer playerMetadata = player.getPersistentDataContainer();
        playerMetadata.set(MenuPage.TITLE_KEY, PersistentDataType.STRING, title); // set menu name metadata

        player.openInventory(createPage(buttons, player));

        playerMetadata.set(MenuPage.ISMENUOPEN_KEY, PersistentDataType.BOOLEAN, true); // set menu flag to open
    }
}
