package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class gcPlayer extends SubCommand {

    public gcPlayer(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages and views information about players.";

        this.subSubCommands = new LinkedHashMap<>() {{
            put("info", new gcPlayerInfo("info", "/gc player info", "gc.player.info"));
            put("settings", new gcPlayerSettings("settings", "/gc player settings", "gc.player.settings"));
        }};
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.subSubCommands.get("info").onCommand(sender, args);
            return;
        }
        findAndExecuteSubSubCommand(sender, args, false);
    }
}
