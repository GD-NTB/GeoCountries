package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public enum SoundEffect {
        CHAT_NOTIF
    }

    public static void playSound(Player player, SoundEffect soundEffect) {;
        if (player == null || !ConfigState.SoundEffects) {
            return;
        }

        Sound sound;
        switch (soundEffect) {
            case CHAT_NOTIF: sound = Sound.BLOCK_NOTE_BLOCK_HARP; break;
            default: return;
        }
        player.playSound(player.getLocation(), sound, 0.75f, 2.0f);
    }
}
