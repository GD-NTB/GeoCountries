package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class gcPlayer extends SubCommand {

    public gcPlayer(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages and views information about players.";
        this.HelpPage   = """
                          §f/gc player [...]: §aManages and views information about players.
                          §f> info [username]: §2Displays info about a particular player.
                          §f> settings [setting?] [value?]: §2Sets/lists your settings""";

        this.subSubCommands = Map.ofEntries(
                Map.entry("info", new gcPlayerInfo("player", "/gc player info", "gc.player.info")),
                Map.entry("settings", new gcPlayerSettings("settings", "/gc player settings", "gc.player.settings"))
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // do /gc player info
            String permission = this.RequiredPermission + ".info";
            if (!sender.hasPermission(permission)) {
                ChatUtil.sendNoPermissionMessage(sender, this.DisplayName + " info", permission);
                return;
            }
            this.subSubCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, false);
    }
}
