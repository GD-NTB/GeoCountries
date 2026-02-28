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

    public gcCitizenshipSent(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
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
                Country country = cApplication.getToCountry();
                String reason = cApplication.reason;
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.reason.substring(0, 20) + "...";

                message.append(Component.text("§a%s§f (§eReason§f: %s§f) "
                                              .formatted(country.name, reason)))
                       // [Unsend] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship unsend " + country.name + "'>" +
                                                       "<hover:show_text:'<white>Click to unsend this citizenship application.</white>'>" +
                                                       "<red><bold>[Unsend]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }
        message.append(Component.text("§6==========================================="));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile player = PlayerProfile.get(sender);
        if (player.hasCitizenship())
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.uuid);
        return cApplications != null && !cApplications.isEmpty();
    }
}
