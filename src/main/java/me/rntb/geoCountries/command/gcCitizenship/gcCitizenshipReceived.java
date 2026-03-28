package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
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

public class gcCitizenshipReceived extends GeoCommand {

    public gcCitizenshipReceived(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Lists received citizenship applications to your country.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.get(sender);

        // if not leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.getPosition() != Position.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of a country can see citizenship applications!");
            return;
        }

        TextComponent.Builder message;
        // no args -> list all applications
        if (args.length == 0)
            message = doCommandList(senderProfile);
        // args -> list specific player
        else {
            // get player
            String otherPlayerName = args[0];
            PlayerProfile otherPlayer = PlayerProfile.get(otherPlayerName);
            if (otherPlayer == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c could not be found!");
                return;
            }

            // get country
            Country country = senderProfile.getCitizenshipObject();
            if (otherPlayer.getCitizenship() != null && otherPlayer.getCitizenship().equals(country.getUUID())) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c is already a citizen of your country!");
                return;
            }

            // get citizenship applications sent by other player
            List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(otherPlayer.getUUID());
            if (cApplications == null || cApplications.isEmpty()) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
                return;
            }

            // get the citizenship application to the sender's country
            CitizenshipApplication cApplication = cApplications.stream()
                                                               .filter(ca -> ca.getToCountry().equals(senderProfile.getCitizenship()))
                                                               .findFirst().orElse(null);
            if (cApplication == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
                return;
            }

            message = doCommandSpecific(cApplication);
        }

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private TextComponent.Builder doCommandList(PlayerProfile senderProfile) {
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATIONS =========="))
               .append(Component.newline());

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(senderProfile.getCitizenship());
        if (cApplications == null || cApplications.isEmpty()) {
            message.append(Component.text("§cYou have not received any citizenship applications."))
                   .append(Component.newline());
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                String reason = cApplication.getReason();
                // truncate
                boolean wasTruncated = false;
                if (reason.length() >= 30) {
                    reason = cApplication.getReason().substring(0, 40) + "...";
                    wasTruncated = true;
                }

                String applicantName = cApplication.getApplicantPlayerProfile().getUsername();
                long daysAgo = TimeUtil.daysAgo(cApplication.getTimeCreated());

                message.append(Component.text("§f> §aFrom§f: §e" + applicantName + " §8(" + daysAgo + " day" + StringUtil.leadingS(daysAgo) + " ago)"))
                       .append(Component.newline())
                       .append(Component.text("§f> §aReason§f: " + reason))
                       .append(Component.newline())

                       // [Accept] button
                       .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicantName + "'>" +
                                                       "<hover:show_text:'<white>Click to accept " + applicantName + "\\'s application.</white>'>" +
                                                       "<green><bold>[Accept]</bold></green>" +
                                                       "</hover></click>"))
                       .append(Component.text("  "));
                // [View All] button
                if (wasTruncated) {
                    message.append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship received " + applicantName + "'>" +
                                                           "<hover:show_text:'<white>Click to view " + applicantName + "\\'s application.</white>'>" +
                                                           "<white><bold>[View All]</bold></white>" +
                                                           "</hover></click>"))
                           .append(Component.text("  "));
                }
                // [Reject] button
                message.append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicantName + "'>" +
                                                       "<hover:show_text:'<white>Click to reject " + applicantName + "\\'s application.</white>'>" +
                                                       "<red><bold>[Reject]</bold></red>" +
                                                       "</hover></click>"))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6==========================================="));

        return message;
    }

    private TextComponent.Builder doCommandSpecific(CitizenshipApplication cApplication) {
        TextComponent.Builder message = Component.text();

        String reason = cApplication.getReason();
        String applicantName = cApplication.getApplicantPlayerProfile().getUsername();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATION =========="))
               .append(Component.newline())

               .append(Component.text("§f> §aFrom§f: §e" + applicantName))
               .append(Component.newline())
               .append(Component.text("§f> §aReason§f: " + reason))
               .append(Component.newline())
               // [Accept] button
               .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicantName + "'>" +
                                               "<hover:show_text:'<white>Click to accept " + applicantName + "\\'s application.</white>'>" +
                                               "<green><bold>[Accept]</bold></green>" +
                                               "</hover></click>"
               ))
               .append(Component.text("  "))
               // [Reject] button
               .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicantName + "'>" +
                                               "<hover:show_text:'<white>Click to reject " + applicantName + "\\'s application.</white>'>" +
                                               "<red><bold>[Reject]</bold></red>" +
                                               "</hover></click>"
               ))
               .append(Component.newline())
               .append(Component.text("§6==========================================="));

        return message;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        if (args.length != 1)
            return List.of();

        PlayerProfile playerProfile = PlayerProfile.get(UuidUtil.getUUIDOfCommandSender(sender));
        if (playerProfile.getPosition() != Position.LEADER)
            return List.of();

        return playerProfile.getCitizenshipObject().getReceivedCitizenshipApplicationsAsUsernames();
    }

    @Override
    public boolean isVisibleOnMenu(CommandSender sender) {
        PlayerProfile playerProfile = PlayerProfile.get(sender);
        if (!playerProfile.hasCitizenship() || playerProfile.getPosition() != Position.LEADER)
            return false;

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(playerProfile.getCitizenship());
        return cApplications != null && !cApplications.isEmpty();
    }
}
