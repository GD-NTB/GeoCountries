package me.rntb.geoCountries.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ItemUtil {

    public enum Skull {
        GREEN_TICK,
        RED_CROSS,
        ONE,
        RED_ONE
    }
    public static String getSkullTextureString(Skull skull) {
        return switch (skull) {
            case GREEN_TICK -> "https://textures.minecraft.net/texture/a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6";
            case RED_CROSS -> "https://textures.minecraft.net/texture/beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7";
            case ONE -> "https://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530";
            case RED_ONE -> "https://textures.minecraft.net/texture/8d2454e4c67b323d5be953b5b3d54174aa271460374ee28410c5aeae2c11f5";
        };
    }
    public static ItemStack getSkull(Skull skull)  {
        ItemStack skullItem = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skullItem.getItemMeta();

        // create dummy player and set its skin
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(getSkullTextureString(skull)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);

        // apply skin to skull
        meta.setPlayerProfile(profile);
        skullItem.setItemMeta(meta);

        return skullItem;
    }
}
