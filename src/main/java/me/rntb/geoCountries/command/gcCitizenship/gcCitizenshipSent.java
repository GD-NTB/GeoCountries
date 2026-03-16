package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcCitizenshipSent extends GeoCommand {

    public gcCitizenshipSent(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists citizenship applications that you have sent.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATIONS =========="))
               .append(Component.newline());

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(((Player) sender).getUniqueId());
        if (cApplications == null || cApplications.isEmpty()) {
            message.append(Component.text("§cYou have not sent any citizenship applications.\n"));
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                Country country = cApplication.getToCountryCountry();
                String reason = cApplication.getReason();
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.getReason().substring(0, 20) + "...";

                message.append(Component.text("§a%s§f (§eReason§f: %s§f) "
                                              .formatted(country.getName(), reason)))
                       // [Unapply] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship unapply " + country.getName() + "'>" +
                                                       "<hover:show_text:'<white>Click to unapply this citizenship application.</white>'>" +
                                                       "<red><bold>[Unapply]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }
        message.append(Component.text("§6==========================================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.hasCitizenship())
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(playerProfile.getUUID());
        return cApplications != null && !cApplications.isEmpty();
    }
}
