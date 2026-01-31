package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class gcCitizenshipReceived {

    public static void onCommand(CommandSender sender, String[] args) {
        PlayerProfile senderProfile = PlayerProfile.byCommandSender(sender);

        // if not leader, escape
        if (!senderProfile.hasCitizenship() || senderProfile.rank != PlayerProfile.PlayerRank.LEADER) {
            ChatUtil.sendPrefixedMessage(sender, "§cOnly the leader of a country can see citizenship applications!");
            return;
        }

        // we must use components as we have clickable elements
        TextComponent.Builder message;
        // no args -> list all applications
        if (args.length == 0)
            message = doCommandList(senderProfile);
        // args -> list specific player
        else {
            // get player
            String otherPlayerName = args[0];
            PlayerProfile otherPlayer = PlayerProfile.byUsername.get(otherPlayerName);
            if (otherPlayer == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c could not be found!");
                return;
            }

            // get country
            Country country = Country.byUUID.get(senderProfile.citizenship);
            if (otherPlayer.citizenship != null && otherPlayer.citizenship.equals(country.uuid)) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c is already a citizen of your country!");
                return;
            }

            // get citizenship applications sent by other player
            List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(otherPlayer.uuid);
            if (cApplications == null || cApplications.isEmpty()) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
                return;
            }

            // get the citizenship application to the sender's country
            CitizenshipApplication cApplication = cApplications.stream()
                                                               .filter(ca -> ca.toCountry.equals(senderProfile.citizenship))
                                                               .findFirst().orElse(null);
            if (cApplication == null) {
                ChatUtil.sendPrefixedMessage(sender, "§cPlayer §f" + otherPlayerName + "§c has not sent a citizen application to your country!");
                return;
            }

            message = doCommandSpecific(cApplication);
        }

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static TextComponent.Builder doCommandList(PlayerProfile senderProfile) {
        TextComponent.Builder message = Component.text();
        MiniMessage mm = MiniMessage.miniMessage();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATIONS =========="))
               .append(Component.newline());

        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(senderProfile.citizenship);
        if (cApplications == null || cApplications.isEmpty()) {
            message.append(Component.text("§cYou have not received any citizenship applications."))
                   .append(Component.newline());
        }
        else {
            for (CitizenshipApplication cApplication : cApplications) {
                String reason = cApplication.reason;
                // truncate
                if (reason.length() >= 30)
                    reason = cApplication.reason.substring(0, 40) + "...";

                String applicantName = PlayerProfile.byUUID.get(cApplication.applicant).username;

                message.append(Component.text("§f> §aFrom§f: §e" + applicantName))
                       .append(Component.newline())
                       .append(Component.text("§f> §aReason§f: " + reason))
                       .append(Component.newline())
                       // [Accept] button
                       .append(mm.deserialize("<click:run_command:'/gc citizenship accept " + applicantName + "'>" +
                                              "<hover:show_text:'<gray>Click to accept</gray> <white>" + applicantName + "</white><gray>\\'s application.</gray>'>" +
                                              "<green><bold>[Accept]</bold></green>" +
                                              "</hover></click>"))
                       .append(Component.text("  "))
                       // [View] button todo: only need this button if truncated
                       .append(mm.deserialize("<click:run_command:'/gc citizenship received " + applicantName + "'>" +
                                               "<hover:show_text:'<dark_gray>Click to view</gray> <white>" + applicantName + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                              "<white><bold>[View]</bold></white>" +
                                              "</hover></click>"))
                       .append(Component.text("  "))
                       // [Reject] button
                       .append(mm.deserialize("<click:run_command:'/gc citizenship reject " + applicantName + "'>" +
                                              "<hover:show_text:'<gray>Click to reject</gray> <white>" + applicantName + "</white><gray>\\'s application.</gray>'>" +
                                              "<red><bold>[Reject]</bold></red>" +
                                              "</hover></click>"))
                       .append(Component.newline());
            }
        }

        message.append(Component.text("§6==========================================="));

        return message;
    }

    public static TextComponent.Builder doCommandSpecific(CitizenshipApplication cApplication) {
        TextComponent.Builder message = Component.text();

        String reason = cApplication.reason;
        String applicantName = PlayerProfile.byUUID.get(cApplication.applicant).username;

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== CITIZENSHIP APPLICATION =========="))
               .append(Component.newline())

               .append(Component.text("§f> §aFrom§f: §e" + applicantName))
               .append(Component.newline())
               .append(Component.text("§f> §aReason§f: " + reason))
               .append(Component.newline())
               // [Accept] button
               .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship accept " + applicantName + "'>" +
                                               "<hover:show_text:'<dark_gray>Click to accept</dark_gray> <white>" + applicantName + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                               "<green><bold>[Accept]</bold></green>" +
                                               "</hover></click>"
               ))
               .append(Component.text("  "))
               // [Reject] button
               .append(ChatUtil.mm.deserialize("<click:run_command:'/gc citizenship reject " + applicantName + "'>" +
                                               "<hover:show_text:'<dark_gray>Click to reject</dark_gray> <white>" + applicantName + "</white><dark_gray>\\'s application.</dark_gray>'>" +
                                               "<red><bold>[Reject]</bold></red>" +
                                               "</hover></click>"
               ))
               .append(Component.newline())
               .append(Component.text("§6==========================================="));

        return message;
    }
}
