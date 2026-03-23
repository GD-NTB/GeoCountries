package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.menu.MenuPage;
import me.rntb.geoCountries.service.CountryService;
import me.rntb.geoCountries.type.Confirmation;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class gcCitizenshipRevoke extends GeoCommand {

    public gcCitizenshipRevoke(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Removes the citizenship of a player of your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if doesnt have citizenship or isn't leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to revoke a player's citizenship!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the name of the player whose citizenship you want to revoke!");
            return;
        }

        String playerName = args[0];

        // if revoking own citizenship, escape
        if (playerName.equals(senderProfile.getUsername())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't revoke your own citizenship, use §f/gc citizenship renounce§c instead!");
            return;
        }

        PlayerProfile playerProfile = PlayerProfile.get(playerName);
        // if player not exist, escape
        if (playerProfile == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // if player is not citizen of sender's country, escape
        if (!playerProfile.getCitizenship().equals(senderProfile.getCitizenship())) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerProfile.getUsername() + "§c is not a citizen of your country!");
            return;
        }

        // todo: check if we are revoking leader inheritor citizenship, that shouldnt happen!

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { playerProfile.getUsername() }),
                                  true);

    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerProfile.get(args[0]);

        Country country = playerProfile.getCitizenshipObject();

        CountryService.leaveCountry(playerProfile);

        ChatUtil.sendPrefixedMessage(sender, "§aRevoked the citizenship of §f" + country.getName() + "§a!");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§f" + playerProfile.getUsername() + "§6 is no longer a citizen of §f" + country.getName() + "§6!", true);
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country country = playerProfile.getCitizenshipObject();
        // should never trigger!
        if (country == null)
            return null;

        List<PlayerProfile> citizens = country.getCitizens().stream()
                                                            .filter(uuid -> !uuid.equals(playerProfile.getUUID()))
                                                            .map(PlayerProfile::get)
                                                            .sorted(Comparator.comparing(PlayerProfile::getPositionLevel))
                                                            .toList().reversed();

        return MenuPage.createSkullMenuButtons(citizens, PlayerProfile::getOfflinePlayer,
                                                         citizen -> "§a" + citizen.getUsername(),
                                                         citizen -> "Revoke §6" + citizen.getUsername() + "§f's citizenship",
                                                         citizen -> "gc citizenship revoke " + citizen.getUsername());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        Country country = PlayerProfile.get(sender).getCitizenshipObject();
        if (country == null)
            return List.of();

        return country.getCitizensAsUsernames();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return playerProfile.getPosition() == Position.LEADER && playerProfile.getCitizenshipObject().getCitizenCount() > 1; // if 1, leader is the only citizen
    }
}
