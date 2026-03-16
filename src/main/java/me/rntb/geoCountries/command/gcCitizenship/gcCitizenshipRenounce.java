package me.rntb.geoCountries.command.gcCitizenship;

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

public class gcCitizenshipRenounce extends GeoCommand {

    public gcCitizenshipRenounce(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Renounces (gives up) citizenship of your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship, escape
        if (!playerProfile.hasCitizenship()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must have citizenship of a country in order to renounce it!");
            return;
        }

        // if leader of country, escape
        if (playerProfile.getPosition() == Position.LEADER) { // todo: this will eventually be replaced by a system where there is a chosen leader inheritor
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't renounce your citizenship if you are the leader of a country, you must either promote another player to leader (§f/gc country promote [player]§c) or dissolve the country (§f/gc country dissolve§c)!");
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
        PlayerProfile playerProfile = PlayerProfile.get(sender);

        Country country = playerProfile.getCitizenshipCountry();
        CountryService.leaveCountry(playerProfile);

        ChatUtil.sendPrefixedNotificationMessage(sender, "§aRenounced your citizenship of §f" + country.getName() + "§a!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.getUsername() + "§6 is no longer a citizen of §f" + country.getName() + "§6!", true);
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return playerProfile.hasCitizenship() && playerProfile.getPosition() != Position.LEADER;
    }
}
