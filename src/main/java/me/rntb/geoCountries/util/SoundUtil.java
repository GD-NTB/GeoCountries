package me.rntb.geoCountries.util;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public enum SoundEffect {
        CHAT_NOTIF,
        MENU_CLICK
    }

    public static void playSound(Player player, SoundEffect soundEffect) {;
        if (!ConfigState.soundEffects || player == null)
            return;
        PlayerProfile toPlayerProfile = PlayerProfile.get(player);
        if (toPlayerProfile == null || toPlayerProfile.getSettings().get("soundeffects").equals("false"))
            return;

        Sound sound;
        float volume = 0.75f;
        float pitch = 1.0f;
        switch (soundEffect) {
            case MENU_CLICK:
                sound = Sound.UI_BUTTON_CLICK;
                volume = 0.25f;
                break;

            case CHAT_NOTIF:
                if (toPlayerProfile.getSettings().get("chatnotificationsounds").equals("false"))
                    return;
                sound = Sound.BLOCK_NOTE_BLOCK_HARP;
                pitch = 2.0f;
                break;

            default:
                return;
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
