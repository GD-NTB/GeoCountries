package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class gcCitizenshipSent {

    public static void onCommand(CommandSender sender, String[] args) {
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATIONS =========="))
               .append(Component.newline());

        Player player = (Player) sender;
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.getUniqueId());
        if (cApplications == null || cApplications.isEmpty()) {
            message.append(Component.text("§cYou have not sent any citizenship applications.\n"));
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                Country country = cApplication.getToCountry();
                String reason = cApplication.reason;
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.reason.substring(0, 20) + "...";

                message.append(Component.text("§a%s§f §f(§eReason§f: %s§f) "
                                              .formatted(country.name, reason)))
                       // [Unsend] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship unsend " + country.name + "'>" +
                                                       "<hover:show_text:'<dark_gray>Click to unsend this citizenship application.</dark_gray>'>" +
                                                       "<red><bold>[Unsend]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }
        message.append(Component.text("§6==========================================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }
}
