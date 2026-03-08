package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcClaimReloadMap extends GeoCommand {

    public gcClaimReloadMap(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Reloads the map on map plugins.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!IntegrationState.isAnyMapPluginEnabled()) {
            ChatUtil.sendPrefixedMessage(sender, "§aThere are no map plugins running on this server!");
            return;
        }

        if (IntegrationState.isPl3xMapEnabled) {
            Pl3xMapIntegration.clearAndDrawAll();
            ChatUtil.sendPrefixedMessage(sender, "§aReloaded claims on Pl3xMap!");
        }
    }
}
