package me.rntb.geoCountries.types;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.command.gc;
import me.rntb.geoCountries.util.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuPage {

    public static Inventory getBaseSubCommandsPage(Player player) {
        List<SubCommand> subCommandsList = new ArrayList<>(gc.allowedSubCommands(player));

        int rows = 2 + (Math.ceilDiv(subCommandsList.size(), 7));
        int slots = 9 * rows;

        subCommandsList.removeIf(sc -> sc.MenuItemMaterial == null); // remove commands with null menu item materials
        subCommandsList.sort(Comparator.comparing(sc -> sc.Name)); // sort by name
        SubCommand[] subCommands = subCommandsList.toArray(SubCommand[]::new);

        Inventory inventory = Bukkit.createInventory(player, slots, Component.text("§8/gc"));

        // 6 commands per row, padded left and right
        int subCommandIndex = 0;
        for (int row = 0; row <= rows-1; row++) {
            for (int col = 0; col <= 8; col++) {
                int flatIndex = row*9 + col;

                // pad top, bottom, left, and right
                // todo: top left or top right needs back/exit button
                if (row == 0 || row == rows-1 || col == 0 || col == 8) {
                    inventory.setItem(flatIndex, ItemStack.of(Material.LIME_STAINED_GLASS_PANE));
                    continue;
                }

                // skip if at end of subcommands
                if (subCommandIndex >= subCommands.length)
                    continue;

                // create itemstack for this command
                SubCommand command = subCommands[subCommandIndex];
                ItemStack item = ItemStack.of(command.MenuItemMaterial);

                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.displayName(Component.text("§a" + StringUtil.sentenceCase(command.Name)));
                itemMeta.lore(List.of(Component.text("§f" + command.HelpString)));
                item.setItemMeta(itemMeta);

                inventory.setItem(flatIndex, item);

                subCommandIndex++;
            }
        }

        return inventory;
    }

//    private static Material randomStainedGlassPane() {
//        Random random = new Random();
//        int i = random.nextInt(1, 9); // 8 bright glass panes
//        return switch (i) {
//            case 1 -> Material.WHITE_STAINED_GLASS_PANE;
//            case 2 -> Material.ORANGE_STAINED_GLASS_PANE;
//            case 3 -> Material.MAGENTA_STAINED_GLASS_PANE;
//            case 4 -> Material.CYAN_STAINED_GLASS_PANE;
//            case 5 -> Material.YELLOW_STAINED_GLASS_PANE;
//            case 6 -> Material.LIME_STAINED_GLASS_PANE;
//            case 7 -> Material.PINK_STAINED_GLASS_PANE;
//            case 8 -> Material.RED_STAINED_GLASS_PANE;
//            default -> Material.GLASS_PANE;
//        };
//    }
}
