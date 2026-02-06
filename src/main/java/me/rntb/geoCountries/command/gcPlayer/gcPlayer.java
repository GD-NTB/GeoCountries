package me.rntb.geoCountries.command.gcPlayer;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.SettingData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcPlayer extends SubCommand {

    public gcPlayer(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages and views information about players.";
        this.HelpPage   = """
                          §f/gc player [...]: §aManages and views information about players.
                          §f> info [username]: §2Displays info about a particular player.
                          §f> settings [setting?] [value?]: §2Sets/lists your settings""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("info", gcPlayerInfo::onCommand),
            Map.entry("settings", gcPlayerSettings::onCommand)
    );

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // do /gc player info
            String permission = this.RequiredPermission + ".info";
            if (!sender.hasPermission(permission)) {
                ChatUtil.sendNoPermissionMessage(sender, this.DisplayName + " info", permission);
                return;
            }
            gcPlayerInfo.onCommand(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, subCommands);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch (args.length) {
            // /gc player [commands]
            case 1 -> subCommands.keySet().stream()
                                          .filter(x -> sender.hasPermission(this.RequiredPermission + "." + x))
                                          .toList();

            // /gc player [command] [...]
            case 2 ->
                switch (args[0]) {
                    // /gc player info [players]
                    case "info" -> sender.hasPermission(this.RequiredPermission + ".info") ? PlayerProfile.allAsUsernames(true) : List.of();

                    // /gc player settings [settings]
                    case "settings" -> {
                        if (!sender.hasPermission(this.RequiredPermission + ".settings"))
                            yield List.of();
                        PlayerProfile playerProfile = PlayerProfile.byCommandSender(sender);
                        if (playerProfile == null)
                            yield List.of();
                        // return all settings as strings
                        yield playerProfile.settings.keySet().stream().toList();
                    }

                    default -> List.of();
                };

            // gc player [command] [...] [...]
            case 3 ->
                switch (args[0]) {
                    // /gc player settings [setting] [value]
                    case "settings" -> {
                        if (!sender.hasPermission(this.RequiredPermission + ".settings"))
                            yield List.of();
                        PlayerProfile playerCountry = PlayerProfile.byCommandSender(sender);
                        if (playerCountry == null)
                            yield List.of();
                        // get settings typed before
                        SettingData settingData = SettingData.get(args[1]);
                        if (settingData == null)
                            yield List.of();
                        // return possible values for this setting
                        yield settingData.getTabAutoCompleteOptions();
                    }

                    default -> List.of();
                };

            default -> List.of();
        };
    }
}
