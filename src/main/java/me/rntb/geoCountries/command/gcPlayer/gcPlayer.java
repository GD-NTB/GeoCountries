package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
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
                Map.entry("info", new gcPlayerInfo("info", "/gc player info", "gc.player.info")),
                Map.entry("settings", new gcPlayerSettings("settings", "/gc player settings", "gc.player.settings"))
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.subSubCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, false);
    }
}
