package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcCountryDissolve extends GeoCommand {

    public gcCountryDissolve(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Dissolves (deletes) your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);

        // if doesnt have citizenship, escape
        if (!player.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (player.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { }),
                                  true);
    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile player = PlayerProfile.get(sender);
        Country country = player.getCitizenshipCountry();

        ChatUtil.sendPrefixedMessage(sender, "§aDissolved country §f" + country.getName() + "§a!");
        ChatUtil.broadcastPrefixedMessage("§6The country §f" + country.getName() + "§6 has just been dissolved!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6Your country has just been dissolved! §cYou are no longer a citizen of any country.", true);

        country.deregister();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return PlayerProfile.get(sender).getPosition() == Position.LEADER;
    }
}
