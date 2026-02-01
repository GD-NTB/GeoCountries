package me.rntb.geoCountries.command.gcCountry;

import me.rntb.geoCountries.command.SubCommand;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.types.Setting;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class gcCountry extends SubCommand {

    public gcCountry(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.HelpPage   = """
                          §f/gc country [...]: §aManage, edit, and view info about all countries.
                          §f> citizens [country?]: §2Lists all citizens of your/any country, their rank, and how many.
                          §f> create [name]: §2Creates a new country.
                          §f> dissolve: §2Dissolves (deletes) your country.;
                          §f> info [country?]: §2Displays info about your/any particular country.
                          §f> list: §2Lists all countries on the server.
                          §f> rename [name]: §2Renames your country.
                          §f> settings [setting?] [value?]: §2Sets/lists your country's settings""";
    }

    private static final Map<String, BiConsumer<CommandSender, String[]>> subCommands = Map.ofEntries(
            Map.entry("citizens", gcCountryCitizens::onCommand),
            Map.entry("create", gcCountryCreate::onCommand),
            Map.entry("dissolve", gcCountryDissolve::onCommand),
            Map.entry("info", gcCountryInfo::onCommand),
            Map.entry("list", gcCountryList::onCommand),
            Map.entry("rename", gcCountryRename::onCommand),
            Map.entry("settings", gcCountrySettings::onCommand)
    );

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /gc country
        if (args.length == 0) {
            // do /gc country info
            if (!sender.hasPermission("gc.country.info")) {
                ChatUtil.sendNoPermissionMessage(sender, "/gc country info", "gc.country.info");
                return;
            }
            gcCountryInfo.onCommand(sender, args);
            return;
        }
        findAndExecuteSubCommand(sender, args, subCommands);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        return switch(args.length) {
            // /gc country [commands]
            case 1 -> subCommands.keySet().stream()
                                          .filter(x -> sender.hasPermission(this.RequiredPermission + "." + x))
                                          .toList();

            // gc country [command] [...]
            case 2 ->
                switch (args[0]) {
                    // /gc country citizens [countries]
                    case "citizens" -> sender.hasPermission(this.RequiredPermission + ".citizens") ? Country.allAsNames(true) : List.of();

                    // /gc country info [countries]
                    case "info" -> sender.hasPermission(this.RequiredPermission + ".info") ? Country.allAsNames(true) : List.of();

                    // /gc country settings [settings]
                    case "settings" -> {
                        if (!sender.hasPermission(this.RequiredPermission + ".settings"))
                            yield List.of();
                        Country playerCountry = Country.byCommandSender(sender);
                        if (playerCountry == null)
                            yield List.of();
                        // return all settings as strings
                        yield Arrays.stream(playerCountry.settings)
                                    .map(s -> s.key)
                                    .toList();
                    }

                    default -> List.of();
                };

            // gc country [command] [...] [...]
            case 3 ->
                switch (args[0]) {
                    // /gc country settings [setting] [value]
                    case "settings" -> {
                        if (!sender.hasPermission(this.RequiredPermission + ".settings"))
                            yield List.of();
                        Country playerCountry = Country.byCommandSender(sender);
                        if (playerCountry == null)
                            yield List.of();
                        // get setting typed before
                        Setting setting = playerCountry.getSetting(args[1]);
                        if (setting == null)
                            yield List.of();
                        // return possible values for this setting
                        yield setting.type == Setting.Type.BOOL ? List.of("true", "false") : List.of();
                    }

                    default -> List.of();
                };

            default -> List.of();
        };
    }
}
