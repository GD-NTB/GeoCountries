package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// todo: unsend button
public class gcCitizenshipSent {

    public static void onCommand(CommandSender sender, String[] args) {
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== CITIZENSHIP APPLICATIONS ==========\n");

        Player player = (Player) sender;
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.getUniqueId());
        if (cApplications == null || cApplications.isEmpty()) {
            sb.append("§cYou have not sent any citizenship applications.\n");
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                Country country = cApplication.getToCountry();
                String reason = cApplication.reason;
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.reason.substring(0, 40) + "...";

                sb.append("§a%s§f §f(§eReason§f: %s§e)\n"
                          .formatted(country.name, reason));
            }
        }
        sb.append("§6===========================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
