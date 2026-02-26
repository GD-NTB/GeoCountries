package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gcDebugSoundTest extends GeoCommand {

    public gcDebugSoundTest(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Plays a sound effect.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        SoundUtil.playSound((Player) sender, SoundUtil.SoundEffect.CHAT_NOTIF);

        ChatUtil.sendPrefixedMessage(sender, "§aDing!");
    }
}
