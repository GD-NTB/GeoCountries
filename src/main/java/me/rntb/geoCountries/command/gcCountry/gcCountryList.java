package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class gcCountryList extends GeoCommand {

    public gcCountryList(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists all countries on the server.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // todo: pages
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== COUNTRY LIST ==========\n");

        if (Country.all.isEmpty()) {
            sb.append("§cThere are no countries.\n");
        }
        else {
            for (Country country : Country.all) {
                PlayerProfile leader = country.getLeader();
                int citizens = country.citizenCount();
                sb.append("§a%s§f (§eLeader§f: %s, §eCitizens§f: %s§f)\n"
                          .formatted(country.name,
                                     leader != null ? country.getLeader().username : "§cNone",
                                     citizens != 0 ? citizens : "§c0"));
            }
        }
        sb.append("§6================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
