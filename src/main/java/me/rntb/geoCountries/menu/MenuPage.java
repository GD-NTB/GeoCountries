package me.rntb.geoCountries.menu;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.metadata.ItemMetadata;
import me.rntb.geoCountries.metadata.PlayerMetadata;
import me.rntb.geoCountries.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.function.Function;

public class MenuPage {

    public static Inventory createPage(ItemStack[] buttons, GeoCommand command, Player player)  {
        boolean isBasePage = command.equals(GeoCommand.getBaseCommand());
        int buttonCount = buttons.length;

        int rows = 2 + (Math.ceilDiv(buttonCount, 7));
        int slots = 9 * rows;

        Inventory inventory = Bukkit.createInventory(player, slots, Component.text("§8" + command.getCommandString()));

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
                            inventory.setItem(flatIndex, createButton(ItemStack.of(Material.ARROW), "§fGo Back", null, command.getParentCommand().getCommandString(), false));
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

    public static ItemStack createButtonOfPlayerSkull(OfflinePlayer player, String name, String description, String command, Boolean isAdminCommand) {
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
                ItemMetadata.setItemCommand(meta, command);
            if (isAdminCommand)
                meta.setEnchantmentGlintOverride(true);

            // hide item hover tooltip shite
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.setHideTooltip(false);
        });

        return newItem;
    }

    public static void openMenuPage(ItemStack[] buttons, GeoCommand command, Player player)  {
        player.openInventory(createPage(buttons, command, player));
        PlayerMetadata.isMenuOpen.put(player.getUniqueId(), true);
    }

    public static void closeMenuPage(Player player) {
        if (!PlayerMetadata.isMenuOpen.get(player.getUniqueId()))
            return;
        Bukkit.getScheduler().runTask(GeoCountries.self, () -> player.closeInventory()); // 1 tick delay
        PlayerMetadata.isMenuOpen.put(player.getUniqueId(), false);
    }

    public static <T> ItemStack[] createSkullMenuButtons(List<T> list, Function<T, OfflinePlayer> player,
                                                                       Function<T, String> name,
                                                                       Function<T, String> description,
                                                                       Function<T, String> runCommand) {
        ItemStack[] buttons = new ItemStack[list.size()];
        int i = 0;
        for (T item : list) {
            try {
                buttons[i] = MenuPage.createButtonOfPlayerSkull(player.apply(item),
                                                                name.apply(item),
                                                                description.apply(item),
                                                                "GUI_RUN_COMMAND:" + runCommand.apply(item),
                                                                false);
            } catch (Exception e) {
                buttons[i] = MenuPage.createButton(ItemStack.of(Material.BARRIER),
                                                                "§cInvalid Entry",
                                                                e.toString(),
                                                                "",
                                                                false);
            }

            i++;
        }

        // truncate nulls because im lazy
        return buttons;
    }
}
