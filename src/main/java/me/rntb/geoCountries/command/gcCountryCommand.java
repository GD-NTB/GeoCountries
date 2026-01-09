package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class gcCountryCommand extends SubCommand {

    public gcCountryCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manages, edits, and views info about all countries.";
        this.HelpPage   = """
                          §f/gc country [...]§a: Manage, edit, and view info about all countries.
                          §f> list: §aLists all countries on the server.
                          §f> info: §2(Case-sensitive) §aDisplays info about a particular country.
                          §f> citizens: §aLists all citizens of a country, their rank, and how many there are.
                          §f> create: §2(Countryless-only) §aCreates a new country.
                          §f> rename: §2(Leader-only) §aRenames your country.
                          §f> dissolve: §2(Leader-only) §aDissolves (deletes) your country.""";
    }

    // todo: make a list of subcommands so we can do autocomplete and help page easier

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aManages, edits, and views info about all countries\n" +
                                                 "Usage: §f/gc country [...]");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc country create
            case "create":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country create");
                    return;
                }
                if (!sender.hasPermission("gc.country.create")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country create", "gc.country.create");
                    return;
                }
                gcCountryCreate(sender, subArgs);
                return;

            // gc country dissolve
            case "dissolve":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country dissolve");
                    return;
                }
                if (!sender.hasPermission("gc.country.dissolve")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country dissolve", "gc.country.dissolve");
                    return;
                }
                gcCountryDissolve(sender, subArgs);
                return;

            case "rename":
                // console cant run
                if (!(sender instanceof Player)) {
                    ChatUtil.SendPrefixedPlayerOnlyErrorMessage("/gc country rename");
                    return;
                }
                if (!sender.hasPermission("gc.country.rename")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country rename", "gc.country.rename");
                    return;
                }
                gcCountryRename(sender, subArgs);
                return;

            // gc country list
            case "list":
                if (!sender.hasPermission("gc.country.list")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country list", "gc.country.list");
                    return;
                }
                gcCountryList(sender, subArgs);
                return;

            // gc country info
            case "info":
                if (!sender.hasPermission("gc.country.info")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country info", "gc.country.info");
                    return;
                }
                gcCountryInfo(sender, subArgs);
                return;

            // gc country citizens
            case "citizens":
                if (!sender.hasPermission("gc.country.citizens")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc country citizens", "gc.country.citizens");
                    return;
                }
                gcCountryCitizens(sender, subArgs);
                return;

            // gc country [xxx]
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc country§c!\n" +
                                                     "Usage: §f/gc country [...]");
                return;
        }
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc country 1
        if (args.length == 1) {
            return List.of("create", "dissolve", "rename", "list", "info", "citizens");
        }
        if (args.length == 2) {
            return switch (args[0]) {
                // /gc country info [countries]
                case "info" -> CountryData.AllAsNames(false);
                // /gc country citizens [countries]
                case "citizens" -> CountryData.AllAsNames(false);
                // /gc country [...]
                default -> List.of();
            };
        }
        return List.of();
    }

    // ---------- /gc country create ----------
    private void gcCountryCreate(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // already in country
        if (pd.hasCountry()) {
            CountryData country = pd.getCountry();
            ChatUtil.SendPrefixedMessage(sender, "§cYou must first renounce your citizenship of §f" + country.Name + "§c using §f/gc citizenship renounce§c before creating a country!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to create!");
            return;
        }

        String countryName = String.join(" ", args);

        // validation check
        CountryData.NameValidation validation = CountryData.ValidateName(countryName, true);
        if (validation != CountryData.NameValidation.OK) {
            ChatUtil.SendPrefixedMessage(sender, CountryData.GetNameValidationString(validation));
            return;
        }

        // start waiting for confirm
        gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                        Triple.of(gcCountryCommand::gcCountryCreateConfirmed,
                                                  sender,
                                                  new String[] { countryName })); // 0 = name
    }

    public static void gcCountryCreateConfirmed(CommandSender sender, String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());

        CountryData newCountry = new CountryData(countryName,
                                                 UUID.randomUUID());
        newCountry.Leader = playerData.Uuid;
        newCountry.Citizens.add(playerData.Uuid);

        // create country
        CountryData.AddNew(newCountry);

        // set player country and rank
        playerData.setCountry(newCountry, PlayerData.PlayerRank.LEADER);

        ChatUtil.SendPrefixedMessage(sender, "§aCreated country §f" + countryName + "§a!");
        ChatUtil.BroadcastPrefixedMessage("§6A new country §f" + countryName + "§6 has just been created!");
    }

    // ---------- /gc country dissolve ----------
    private void gcCountryDissolve(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // if not in country, escape
        if (!pd.hasCountry()) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of a country to dissolve it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of your country to dissolve it!");
            return;
        }

        // start waiting for confirm
        gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                        Triple.of(gcCountryCommand::gcCountryDissolveConfirmed,
                                                  sender,
                                                  new String[] { }));
    }

    private static void gcCountryDissolveConfirmed(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());
        CountryData country = playerData.getCountry();

        ChatUtil.SendPrefixedMessage(sender, "§aDissolved country §f" + country.Name + "§a!");
        ChatUtil.BroadcastPrefixedMessage("§6The country §f" + country.Name + "§6 has just been dissolved!");

        CountryData.Delete(country);
    }

    // ---------- /gc country rename ----------
    private void gcCountryRename(@NotNull CommandSender sender, @NotNull String[] args) {
        UUID playerUUID = ((Player) sender).getUniqueId();
        PlayerData pd = PlayerData.PlayerDataByUUID.get(playerUUID);

        // if not in country, escape
        if (!pd.hasCountry()) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of a country to rename it!");
            return;
        }

        // if not leader of country, escape
        if (pd.getLeaderOf() == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must be the leader of your country to change its name!");
            return;
        }

        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the new name of the country!");
            return;
        }

        String countryName = String.join(" ", args);

        // validation check
        CountryData.NameValidation validation = CountryData.ValidateName(countryName, true);
        if (validation != CountryData.NameValidation.OK) {
            ChatUtil.SendPrefixedMessage(sender, CountryData.GetNameValidationString(validation));
            return;
        }

        // start waiting for confirm
        gcConfirmCommand.WaitForConfirm(UuidUtil.GetUUIDOfCommandSender(sender),
                                        Triple.of(gcCountryCommand::gcCountryRenameConfirmed,
                                                  sender,
                                                  new String[] { countryName })); // 0 = name
    }

    private static void gcCountryRenameConfirmed(CommandSender sender, String[] args) {
        String countryName = args[0];
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.PlayerDataByUUID.get(player.getUniqueId());
        CountryData country = playerData.getCountry();

        ChatUtil.BroadcastPrefixedMessage("§6The country of §f" + country.Name + "§6 has been renamed to §f" + countryName + "§6!");

        country.setName(countryName);

        ChatUtil.SendPrefixedMessage(sender, "§aRenamed country to §f" + countryName + "§a!");
    }

    // ---------- /gc country list ----------
    private void gcCountryList(@NotNull CommandSender sender, @NotNull String[] args) {
        // todo: pages
        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                                            "§6========== COUNTRY LIST ==========\n");

        if (CountryData.All.isEmpty()) {
            sb.append("§cThere are no countries.\n");
        }
        else {
            for (CountryData country : CountryData.All) {
                PlayerData leader = country.getLeader();
                int citizens = country.CitizenCount();
                sb.append(String.format("§a%s§f (§eLeader§f: %s, §eCitizens§f: %s)\n",
                                        country.Name, leader != null ? country.getLeader().Username : "§cNone", citizens != 0 ? citizens : "§c0"));
            }
        }
        sb.append("§6=================================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }

    // ---------- /gc country info ----------
    private void gcCountryInfo(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to get info of!");
            return;
        }

        String countryName = String.join(" ", args);

        CountryData country = CountryData.CountryDataByName.get(countryName);
        if (country == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cCountry \"§f" + countryName + "§c\" could not be found!");
            return;
        }

        PlayerData leader = country.getLeader();

        String sb = ChatUtil.NewlineIfPrefixIsEmpty() +
                   "§6========== COUNTRY INFO ==========\n" +
                    String.format("§a%s§f\n" +
                                  "> §eLeader§f: %s§f\n" +
                                  "> §eCitizens§f: %s§f\n",
                                  country.Name, leader != null ? leader.Username : "§cNone", country.CitizenCount()) +
                   "§6=================================";
        ChatUtil.SendPrefixedMessage(sender, sb);
    }

    // ---------- /gc country citizens ----------
    // todo: break into pages (page number as arg)
    private void gcCountryCitizens(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the country you want to get the list of citizens of!");
            return;
        }

        String countryName = String.join(" ", args);

        CountryData country = CountryData.CountryDataByName.get(countryName);
        if (country == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cCountry \"§f" + countryName + "§c\" could not be found!");
            return;
        }

        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                                            "§6========== COUNTRY CITIZENS ==========");

        int citizenCount = country.CitizenCount();
        if (citizenCount == 0) {
            sb.append("§cThere are no citizens of this country.\n");
        }
        else {
            sb.append("§e").append(citizenCount).append(" §fcitizens:\n");
            for (PlayerData citizen : country.CitizensSortedByRank()) {
                sb.append(String.format("§f> §a%s§f (§e%s§f)\n",
                                        citizen.Username, citizen.getRankString()));
            }
        }
        sb.append("§6======================================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }
}
