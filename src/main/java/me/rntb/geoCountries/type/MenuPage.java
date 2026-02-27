package me.rntb.geoCountries.type;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// todo: move this somewhere out of types package
public class MenuPage {

    // todo: move these to some metadata class (in package "class"?)
    // item metadata
    public static final NamespacedKey ITEM_COMMAND_KEY = new NamespacedKey(GeoCountries.self, "command");
    // player metadata
    public static final HashMap<UUID, Boolean> playerIsMenuOpen = new HashMap<>();
    public static final HashMap<UUID, String> playerPreviousPage = new HashMap<>();

    // todo: buttons that we don't have permission for (e.g. not the correct rank) need to be hidden (GeoCommand.isUsable?)
    public static Inventory createPage(ItemStack[] buttons, String title, Player player, boolean isBasePage)  {
        int buttonCount = buttons.length;

        int rows = 2 + (Math.ceilDiv(buttonCount, 7));
        int slots = 9 * rows;

        Inventory inventory = Bukkit.createInventory(player, slots, Component.text("§8" + title));

        // 6 commands per row, padded left and right
        int childCommandIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int flatIndex = row*9 + col;

                // bottom row
                if (row == rows-1) {
                    // set confirm button
                    if (isBasePage && col == 6) {
                        inventory.setItem(flatIndex, createButton(ItemUtil.getSkull(ItemUtil.Skull.GREEN_TICK), "§a/gc confirm", "Confirms a pending command/action.", "/gc confirm", false));
                        continue;
                    }

                    // set cancel button
                    if (isBasePage && col == 7) {
                        inventory.setItem(flatIndex, createButton(ItemUtil.getSkull(ItemUtil.Skull.RED_CROSS), "§c/gc cancel", "Cancels a pending command/action. ", "/gc cancel", false));
                        continue;
                    }

                    if (col == 8) {
                        // set close button
                        if (isBasePage)
                            inventory.setItem(flatIndex, createButton(ItemStack.of(Material.BARRIER), "§cClose", null, "GUI_CLOSE", false));
                        // set back button
                        else
                            inventory.setItem(flatIndex, createButton(ItemStack.of(Material.ARROW), "§fGo Back", null, MenuPage.playerPreviousPage.get(player.getUniqueId()), false));
                        continue;
                    }

                    // else pad bottom
                    inventory.setItem(flatIndex, getPaddingButton());
                    continue;
                }

                // pad top, left, right
                if (row == 0 || col == 0 || col == 8) {
                    inventory.setItem(flatIndex, getPaddingButton());
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

    public static ItemStack getPaddingButton() {
        return createButton(ItemStack.of(Material.LIME_STAINED_GLASS_PANE), "", null, null, false);
    }

    public static ItemStack createButtonOfPlayerSkull(Player player, String name, String description, String command, Boolean isAdminCommand) {
        ItemStack skullItem = ItemStack.of(Material.PLAYER_HEAD);
        skullItem.editMeta(meta -> ((SkullMeta) meta).setOwningPlayer(player));
        return createButton(skullItem, name, description, command, isAdminCommand);
    }
    public static ItemStack createButton(ItemStack item, String name, String description, String command, Boolean isAdminCommand) {
        ItemStack newItem = item.clone();

        newItem.editMeta(meta -> {
            if (name != null) {
                String titleColour = isAdminCommand ? "§6" : "§a";
                meta.displayName(Component.text(titleColour + name));
            }
            if (description != null)
                meta.lore(List.of(Component.text("§f" + description)));
            if (command != null)
                meta.getPersistentDataContainer().set(ITEM_COMMAND_KEY, PersistentDataType.STRING, command);
            if (isAdminCommand)
                meta.setEnchantmentGlintOverride(true);

            // hide item hover tooltip shite
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.setHideTooltip(false);
        });

        return newItem;
    }

    public static void openMenuPage(ItemStack[] buttons, String title, Player player, boolean isBasePage)  {
        player.openInventory(createPage(buttons, title, player, isBasePage));
        MenuPage.playerIsMenuOpen.put(player.getUniqueId(), true);
    }

    public static void closeMenuPage(Player player) {
        if (!MenuPage.playerIsMenuOpen.get(player.getUniqueId()))
            return;
        Bukkit.getScheduler().runTask(GeoCountries.self, () -> player.closeInventory()); // 1 tick delay
        MenuPage.playerIsMenuOpen.put(player.getUniqueId(), false);
    }
}
