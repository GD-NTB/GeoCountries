package me.rntb.geoCountries.command.gcCountry;

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

public class gcCountryTransfer extends GeoCommand {

    public gcCountryTransfer(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Transfers the leadership of your country to a citizen.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        Country senderCountry = senderProfile.getCitizenshipCountry();
        // if doesnt have citizenship or isn't leader, escape
        if (senderCountry == null || senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be the leader of a country to transfer leadership!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must be put the name of the citizen who you want to transfer leadership to!");
            return;
        }

        String playerName = args[0];

        // if transferring to self, escape
        if (playerName.equals(senderProfile.getUsername())) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou can't transfer leadership to yourself, obviously!");
            return;
        }

        PlayerProfile newLeader = PlayerProfile.get(playerName);
        // if player not exist, escape
        if (newLeader == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        // if player is not citizen of sender's country, escape
        if (newLeader.getCitizenship() != null || !newLeader.getCitizenship().equals(senderCountry.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c must be a citizen of your country before you can transfer leadership to them!");
            return;
        }

        // start waiting for confirm
        Confirmation.startWaiting(UuidUtil.getUUIDOfCommandSender(sender),
                                  new Confirmation(this::onConfirm,
                                                   sender,
                                                   new String[] { playerName }),
                                  true);

    }

    private void onConfirm(CommandSender sender, String[] args) {
        PlayerProfile newLeader = PlayerProfile.get(args[0]);

        Country country = newLeader.getCitizenshipCountry();

        CountryService.promoteToLeader(newLeader);

        ChatUtil.sendPrefixedMessage(sender, "§aTransferred country leadership to §f" + newLeader.getUsername() + "§a! §cYou are no longer the leader.");

        // broadcast notif to country
        ChatUtil.broadcastPrefixedMessageToCountry(country, "§6The leadership of your country has changed, the new leader of §f" + country.getName() + "§6 is §f" + newLeader.getUsername() + "§6!", true);

        // send notif to new leader
        ChatUtil.sendPrefixedMessage(newLeader.getOnlinePlayer(), "§6You are now the new leader of §f" + country.getName() + "§6!");
    }

    @Override
    public ItemStack[] getMenuButtons(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country country = playerProfile.getCitizenshipCountry();
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
                                                         citizen -> "Transfer leadership to §6" + citizen.getUsername(),
                                                         citizen -> "gc country transfer " + citizen.getUsername());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(sender);
        Country country = playerProfile.getCitizenshipCountry();
        if (country == null)
            return List.of();

        // goo goo ga ga
        return country.getCitizens().stream()
                                    .filter(uuid -> !uuid.equals(playerProfile.getUUID()))
                                    .map(PlayerProfile::get)
                                    .sorted(Comparator.comparing(PlayerProfile::getPositionLevel))
                                    .map(PlayerProfile::getUsername)
                                    .toList().reversed();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        return playerProfile.getPosition() == Position.LEADER && playerProfile.getCitizenshipCountry().getCitizenCount() > 1; // if 1, leader is the only citizen
    }
}
