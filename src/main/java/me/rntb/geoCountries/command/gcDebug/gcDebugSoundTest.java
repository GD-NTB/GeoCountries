package me.rntb.geoCountries.command.gcDebug;

import me.rntb.geoCountries.command.SubSubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.SoundUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcDebugSoundTest extends SubSubCommand {

    public gcDebugSoundTest(String name, String displayName, String requiredPermission) {
        super(name, displayName, requiredPermission);
        this.HelpString = "Plays a sound effect.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendPrefixedPlayerOnlyErrorMessage("/gc debug soundtest");
            return;
        }

        SoundUtil.playSound(player, SoundUtil.SoundEffect.CHAT_NOTIF);

        ChatUtil.sendPrefixedMessage(sender, "§aDing!");
    }
}
