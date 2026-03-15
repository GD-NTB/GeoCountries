package me.rntb.geoCountries.util;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UuidUtil {
    public static UUID getUUIDOfCommandSender(CommandSender sender) {
        return sender instanceof ConsoleCommandSender ? UUID.fromString("00000000-0000-0000-0000-000000000000") :
                                                        ((Player) sender).getUniqueId();
    }
}
