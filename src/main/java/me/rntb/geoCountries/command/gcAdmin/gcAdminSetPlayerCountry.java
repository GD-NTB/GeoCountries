package me.rntb.geoCountries.command.gcAdmin;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.service.CountryService;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class gcAdminSetPlayerCountry extends GeoCommand {

    public gcAdminSetPlayerCountry(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Sets a player's country";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the name of the player you want to change the country of!");
            return;
        }

        if (args.length == 1) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou must put the new country you want the player to be a citizen of!");
            return;
        }

        String playerName = args[0];
        PlayerProfile playerProfile = PlayerProfile.get(playerName);

        if (playerProfile == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + playerName + "§c could not be found!");
            return;
        }

        String countryName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // set country
        if (playerProfile.getPosition() == Position.LEADER)
            ChatUtil.sendPrefixedMessage(sender, "§aSet new leader to random citizen or dissolved.");

        if (countryName.equals("null")) {
            CountryService.leaveCountry(playerProfile);
        }
        else {
            Country country = Country.get(countryName);
            if (country == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cCountry §f" + countryName + "§c does not exist!");
                return;
            }

            // set country
            CountryService.joinCountry(playerProfile, country);
        }

        ChatUtil.sendPrefixedMessage(sender, "§aSet player country!");
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return List.of();
    }
}
