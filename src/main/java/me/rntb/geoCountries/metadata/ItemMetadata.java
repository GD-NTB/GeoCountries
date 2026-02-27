package me.rntb.geoCountries.metadata;

import me.rntb.geoCountries.GeoCountries;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemMetadata {

    private static final NamespacedKey ITEM_COMMAND_KEY = new NamespacedKey(GeoCountries.self, "command");
    public static String getItemCommand(ItemStack item) {
        return item.getPersistentDataContainer().get(ITEM_COMMAND_KEY, PersistentDataType.STRING);
    }
    public static void setItemCommand(ItemMeta meta, String value) {
        meta.getPersistentDataContainer().set(ITEM_COMMAND_KEY, PersistentDataType.STRING, value);
    }
}
