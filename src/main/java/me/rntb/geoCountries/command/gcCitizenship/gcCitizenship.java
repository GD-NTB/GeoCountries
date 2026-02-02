package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class gcCitizenship extends SubCommand {

    public gcCitizenship(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages your citizenship and your citizens.";
        this.HelpPage   = """
                          §f/gc citizenship [...]§a: Manages your citizenship and your country's citizens.
                          §f> accept: §2Accepts a player's citizenship application to your country.
                          §f> apply: §2Applies for citizenship to a country.
                          §f> received: §2Lists received citizenship applications to your country.
                          §f> reject: §2Rejects a player's citizenship application to your country.
                          §f> renounce: §2Renounces (gives up) citizenship of your country.
                          §f> revoke: §2Revoke's the citizenship of a player of your country.
                          §f> sent: §2Lists citizenship applications that you have sent.
                          §f> unsend: §2Unsends a citizenship application that you previously sent.""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("accept", gcCitizenshipAccept::onCommand),
            Map.entry("apply", gcCitizenshipApply::onCommand),
            Map.entry("received", gcCitizenshipReceived::onCommand),
            Map.entry("reject", gcCitizenshipReject::onCommand),
            Map.entry("renounce", gcCitizenshipRenounce::onCommand),
            Map.entry("revoke", gcCitizenshipRevoke::onCommand),
            Map.entry("sent", gcCitizenshipSent::onCommand),
            Map.entry("unsend", gcCitizenshipUnsend::onCommand)
    );

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendPrefixedMessage(sender, """
                                                 §a%s
                                                 Usage: §f%s [...]"""
                                                 .formatted(this.HelpString, this.DisplayName));
            return;
        }
        findAndExecuteSubCommand(sender, args, subCommands);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return switch (args.length) {
            // /gc citizen [commands]
            case 1 -> subCommands.keySet().stream()
                                          .filter(x -> sender.hasPermission(this.RequiredPermission + "." + x))
                                          .toList();

            // gc citizen [command] [...]
            case 2 ->
                switch (args[0]) {
                    // /gc citizenship accept [players]
                    case "accept" -> {
                        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
                        if (player.rank != PlayerProfile.PlayerRank.LEADER || !sender.hasPermission(this.RequiredPermission + ".accept"))
                            yield List.of();
                        yield getReceivedCitizenshipApplicationsAsStrings(player.citizenship);
                    }

                    // /gc citizenship reject [players]
                    case "reject" -> {
                        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
                        if (player.rank != PlayerProfile.PlayerRank.LEADER || !sender.hasPermission(this.RequiredPermission + ".reject"))
                            yield List.of();
                        yield getReceivedCitizenshipApplicationsAsStrings(player.citizenship);
                    }

                    // /gc citizenship apply [countries]
                    case "apply" -> sender.hasPermission(this.RequiredPermission + ".apply") ? Country.allAsNames(true) : List.of();

                    // /gc citizenship unsend [countries]
                    case "unsend" -> {
                        if (!sender.hasPermission(this.RequiredPermission + ".unsend"))
                            yield List.of();
                        Player player = (Player) sender;
                        yield getSentCitizenshipApplicationsAsStrings(player.getUniqueId());
                    }

                    // /gc citizenship received [players]
                    case "received" -> {
                        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
                        if (player.rank != PlayerProfile.PlayerRank.LEADER || !sender.hasPermission(this.RequiredPermission + ".received"))
                            yield List.of();
                        yield getReceivedCitizenshipApplicationsAsStrings(player.citizenship);
                    }

                    // /gc citizenship revoke [players]
                    case "revoke" -> {
                        PlayerProfile player = PlayerProfile.byUUID.get(UuidUtil.getUUIDOfCommandSender(sender));
                        if (player.rank != PlayerProfile.PlayerRank.LEADER || !sender.hasPermission(this.RequiredPermission + ".revoke"))
                            yield List.of();
                        yield player.getCitizenship().citizens.stream()
                                                              .map(c -> PlayerProfile.byUUID.get(c).username)
                                                              .toList();
                    }

                    default -> List.of();
                };

            default -> List.of();
        };
    }

    private static List<String> getSentCitizenshipApplicationsAsStrings(UUID fromPlayer) {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByApplicant.get(fromPlayer);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getToCountry().name)
                            .toList();
    }

    private static List<String> getReceivedCitizenshipApplicationsAsStrings(UUID toCountry) {
        List<CitizenshipApplication> cApplications = CitizenshipApplication.sentByToCountry.get(toCountry);
        if (cApplications == null)
            return List.of();
        return cApplications.stream()
                            .map(ca -> ca.getApplicant().username)
                            .toList();
    }
}
