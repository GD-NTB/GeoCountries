package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcCountryRename extends GeoCommand {

    public gcCountryRename(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Renames your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to rename it!");
            return;
        }

        // if not leader of country, escape
        if (playerProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of the country to change its name!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§6What do you want the new name of your country to be?");
            // start waiting for response
            Response.startWaiting(playerProfile.getUUID(),
                                  new Response(this::onResponse,
                                               sender),
                                  true);
        }
        else {
            String countryName = String.join(" ", args).trim();
            onResponse(sender, countryName);
        }
    }

    private void onResponse(CommandSender sender, String countryName) {
        // validation check
        String validationString = StringUtil.validateCountryName(countryName, true);
        if (validationString != null) {
            ChatUtil.sendPrefixedMessage(sender, validationString);
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { countryName }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        String countryName = args[0];
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country country = playerProfile.getCitizenshipObject();

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aRenamed country to §f" + countryName + "§a!");

        ChatUtil.broadcastPrefixedMessage("§6The country §f" + country.getName() + "§6 has been renamed to §f" + countryName + "§6!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6Your country has now been renamed to §f" + countryName + "§6!", true);

        country.setName(countryName);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
