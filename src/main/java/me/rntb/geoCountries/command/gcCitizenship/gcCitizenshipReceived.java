package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// todo: register zis blyat
public class gcCitizenshipReceived {

    public static void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile playerProfile = PlayerProfile.get(player);

        // if not leader, escape
        if (playerProfile.rank != PlayerProfile.PlayerRank.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly a leader of a country can see citizenship applications!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                             "§6========== CITIZENSHIP APPLICATIONS ==========\n");

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(playerProfile.citizenship);
        if (cApplications == null || cApplications.isEmpty()) {
            sb.append("§cYou have not received any citizenship applications.\n");
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                Country country = cApplication.getToCountry();
                String reason = cApplication.reason;
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.reason.substring(0, 40) + "...";

                String applicantName = PlayerProfile.byUUID.get(cApplication.applicant).username;

                sb.append("""
                        §f> §aFrom§f: §e%s§f
                        §f> §aReason§f: §e%s
                        """
                        .formatted(applicantName, reason));
            }
        }
        sb.append("§6===========================================");
        ChatUtil.sendPrefixedMessage(sender, String.valueOf(sb));
    }
}
