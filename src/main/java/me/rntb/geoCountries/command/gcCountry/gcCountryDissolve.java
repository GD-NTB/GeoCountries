package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CountryService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcCountryDissolve extends GeoCommand {

    public gcCountryDissolve(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Dissolves (deletes) your country.");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[0]),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Country country = playerProfile.getCitizenshipObject();
        if (country == null)
            return;

        ChatUtil.sendPrefixedMessage(sender, "§aDissolved country §f" + country.getName() + "§a!");

        CountryService.dissolve(country);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
