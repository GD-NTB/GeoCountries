package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class gcCitizenship extends SubCommand {

    public gcCitizenship(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages your citizenship and your country's citizens.";
        this.HelpPage   = """
                          §f/gc citizenship [...]§a: Manages your citizenship and your country's citizens.
                          §f> apply: §2Applies for citizenship to a country.
                          §f> received: §2Lists received citizenship applications to your country.
                          §f> renounce: §2Renounces (gives up) citizenship of your country.
                          §f> sent: §2Lists citizenship applications that you have sent.
                          §f> unsend: §2Unsends a citizenship application that you previously sent.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /gc citizenship
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }

        String mode = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // /gc citizenship apply
            case "apply":
                if (!sender.hasPermission("gc.citizenship.apply")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship apply", "gc.citizenship.apply");
                    return;
                }
                gcCitizenshipApply.onCommand(sender, subArgs);
                return;

            // /gc citizenship sent
            case "sent":
                if (!sender.hasPermission("gc.citizenship.sent")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship sent", "gc.citizenship.sent");
                    return;
                }
                gcCitizenshipSent.onCommand(sender, subArgs);
                return;

            // /gc citizenship unsend
            case "unsend":
                if (!sender.hasPermission("gc.citizenship.unsend")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship unsend", "gc.citizenship.unsend");
                    return;
                }
                gcCitizenshipUnsend.onCommand(sender, subArgs);
                return;

            // /gc citizenship renounce
            case "renounce":
                if (!sender.hasPermission("gc.citizenship.renounce")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship renounce", "gc.citizenship.renounce");
                    return;
                }
                gcCitizenshipRenounce.onCommand(sender, subArgs);
                return;

            // /gc citizenship received
            case "received":
                if (!sender.hasPermission("gc.citizenship.received")) {
                    ChatUtil.sendNoPermissionMessage(sender, "/gc citizenship received", "gc.citizenship.received");
                    return;
                }
                gcCitizenshipReceived.onCommand(sender, subArgs);
                return;

            // gc citizenship [xxx]
            default:
                ChatUtil.sendPrefixedMessage(sender, """
                                                     §c§f%s§c is not a valid command for §f%s§c!
                                                     Usage: §f%s [...]"""
                                                     .formatted(mode, this.DisplayName, this.DisplayName));
                return;
        }
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc citizen 1
            case 1 -> Stream.of("apply", "received", "renounce", "sent", "unsend").filter(x -> sender.hasPermission("gc.citizenship." + x)).toList();
            // gc citizen [...] 2
            case 2 ->
                switch (args[0]) {
                    // /gc citizenship apply [countries]
                    case "apply" -> sender.hasPermission("gc.citizenship.apply") ? Country.allAsNames(true) : List.of();
                    // /gc citizenship unsend [countries]
                    case "unsend" -> {
                        if (!sender.hasPermission("gc.citizenship.unsend"))
                            yield List.of();
                        Player player = (Player) sender;
                        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(player.getUniqueId());
                        if (cApplications == null)
                            yield List.of();
                        yield cApplications.stream().map(ca -> ca.getToCountry().name).toList();
                    }
                    // /gc citizenship [...]
                    default -> List.of();
                };
            default -> List.of();
        };
    }
}
