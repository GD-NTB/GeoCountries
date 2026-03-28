package me.rntb.geoCountries.command.gcFaction;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.FactionInvite;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import me.rntb.geoCountries.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class gcFactionSent extends GeoCommand {

    public gcFactionSent(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Lists sent faction invites from your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        Country country = senderProfile.getCitizenshipObject();
        if (country == null) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of a faction can see sent faction invites!");
            return;
        }
        Faction faction = country.getFactionObject();
        if (faction == null || !faction.getLeader().equals(country.getUUID())) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of a faction can see sent faction invites!");
            return;
        }
        // if not leader, escape
        if (senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of the faction can see sent faction invites!");
            return;
        }

        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== FACTION INVITES =========="))
               .append(Component.newline());

        List<FactionInvite> fInvites = FactionInvite.byFromFaction.get(faction.getUUID());
        if (fInvites == null || fInvites.isEmpty()) {
            message.append(Component.text("§cYou have not sent any faction invites."))
                   .append(Component.newline());
        }
        else {
            for (FactionInvite fInvite : fInvites) {
                String toCountryName = fInvite.getToCountryObject().getName();
                long daysAgo = TimeUtil.daysAgo(fInvite.getTimeCreated());

                message.append(Component.text("§a" + toCountryName + "§f §8(" + daysAgo + " day" + StringUtil.leadingS(daysAgo) + " ago)  "))
                       // [Uninvite] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc faction uninvite " + toCountryName + "'>" +
                                                       "<hover:show_text:'<white>Click to unsend this faction invite.</white>'>" +
                                                       "<red><bold>[Uninvite]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6==========================================="));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        Country country = PlayerProfile.get(sender).getCitizenshipObject();
        if (country == null)
            return List.of();
        UUID factionUUID = country.getFaction();
        if (factionUUID == null)
            return List.of();

        List<FactionInvite> fInvitesSent = FactionInvite.byFromFaction.get(factionUUID);
        if (fInvitesSent == null)
            return List.of();

        return fInvitesSent.stream()
                           .map(fi -> fi.getToCountryObject().getName()).toList();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        return !getTabCompletion(sender, new String[] { "" }).isEmpty();
    }
}
