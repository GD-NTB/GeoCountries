package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.MenuPage;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class gcGui extends SubCommand {

    public gcGui(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Opens the plugin's GUI menu.";
        this.HelpPage   = """
                          §f/gc dump: §aOpens the GeoCountries visual GUI menu.""";;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        player.openInventory(MenuPage.getBaseSubCommandsPage(player));

        ChatUtil.sendPrefixedMessage(sender, "§aOpening menu...");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return List.of();
    }
}
