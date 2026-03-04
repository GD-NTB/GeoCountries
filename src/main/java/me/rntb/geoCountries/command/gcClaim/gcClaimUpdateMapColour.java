package me.rntb.geoCountries.command.gcClaim;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.Pl3xMapIntegration;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcClaimUpdateMapColour extends GeoCommand {

    public gcClaimUpdateMapColour(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Updates the colour of claims on map plugins.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        Country country = player.getCitizenship();
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to run this command!");
            return;
        }
        if (player.position != PlayerProfile.Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to run this command!");
            return;
        }

        if (!IntegrationState.isAnyMapPluginEnabled()) {
            ChatUtil.sendPrefixedMessage(sender, "§aThere are no map plugins running on this server!");
            return;
        }

        if (IntegrationState.isPl3xMapEnabled) {
            Pl3xMapIntegration.reloadClaimsColourOfCountry(country);
            ChatUtil.sendPrefixedMessage(sender, "§aReloaded colour of claims on Pl3xMap!");
        }

        ChatUtil.sendPrefixedMessage(sender, "§aFinished!");
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).position == PlayerProfile.Position.LEADER;
    }
}
