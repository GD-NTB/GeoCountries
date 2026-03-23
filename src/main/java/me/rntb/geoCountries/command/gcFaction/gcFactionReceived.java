package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.TimeUtil;
import me.rntb.geoCountries.util.UuidUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class gcFactionReceived extends GeoCommand {

    public gcFactionReceived(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists received faction invites to your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if not leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of a country can see received faction invites!");
            return;
        }

        Country country = senderProfile.getCitizenshipObject();
        if (country.hasFaction()) {
            ChatUtil.sendPrefixedMessage(sender, "§cYou already have a faction!");
            return;
        }

        // list all faction invites
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== FACTION INVITES =========="))
               .append(Component.newline());

        List<FactionInvite> fInvites = FactionInvite.byToCountry.get(country.getUUID());
        if (fInvites == null || fInvites.isEmpty()) {
            message.append(Component.text("§cYou have not received any faction invites."))
                   .append(Component.newline());
        }
        else {
            for (FactionInvite fInvite : fInvites) {
                String fromFactionName = fInvite.getFromFactionObject().getName();
                String fromCountryName = fInvite.getFromCountryObject().getName();
                long daysAgo = TimeUtil.daysAgo(fInvite.getTimeCreated());

                message.append(Component.text("§f> §aFrom faction§f: §3" + fromFactionName + " §8(" + daysAgo + " day" + StringUtil.leadingS(daysAgo) + " ago)"))
                       .append(Component.newline())
                       .append(Component.text("§f> §aBy country§f: " + fromCountryName))
                       .append(Component.newline())

                       // [Accept] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc faction accept " + fromFactionName + "'>" +
                                                       "<hover:show_text:'<white>Click to join " + fromFactionName + ".</white>'>" +
                                                       "<green><bold>[Accept]</bold></green>" +
                                                       "</hover></click>"))
                       .append(Component.text("  "))
                       // [Decline] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc faction decline " + fromFactionName + "'>" +
                                                       "<hover:show_text:'<white>Click to decline " + fromFactionName + ".</white>'>" +
                                                       "<red><bold>[Decline]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6==================================="));


        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (playerProfile.getPosition() != Position.LEADER)
            return List.of();

        List<FactionInvite> fInvites = FactionInvite.byToCountry.get(playerProfile.getCitizenshipObject().getUUID());
        if (fInvites == null || fInvites.isEmpty())
            return List.of();

        return fInvites.stream()
                       .map(fi -> fi.getFromFactionObject().getName()).toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (playerProfile.getPosition() != PlayerProfile.Position.LEADER)
            return false;

        Country country = playerProfile.getCitizenshipObject();
        if (country == null)
            return false;

        List<FactionInvite> fInvites = FactionInvite.byToCountry.get(country.getUUID());
        return fInvites != null && !fInvites.isEmpty();
    }
}
