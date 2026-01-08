package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CountryData;
import me.rntb.geoCountries.data.PlayerData;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class gcPlayerCommand extends SubCommand {

    public gcPlayerCommand(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Manage and view information about players.";
        this.HelpPage   = "§f/gc player [...]§a: Manage and view information about players.§r"; // todo
    }

    @Override
    void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc country
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§aManage and view information about players.\n" +
                                                 "Usage: §f/gc player [info/...]§r");
            return;
        }

        String mode = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // find and route to proper method
        switch (mode) {
            // gc player info
            case "info":
                if (!sender.hasPermission("gc.player.info")) {
                    ChatUtil.SendNoPermissionMessage(sender, "/gc player info", "gc.player.info");
                    return;
                }
                gcPlayerInfo(sender, subArgs);
                return;

            // gc player [xxx]
            default:
                ChatUtil.SendPrefixedMessage(sender, "§c\"§f" + mode + "§c\" is not a valid command for §f/gc player§c!\n" +
                                                     "Usage: §f/gc player [info/...]§r");
                return;
        }
    }

    @Override
    List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        // /gc player 1
        if (args.length == 1) {
            return List.of("info");
        }
        if (args.length == 2) {
            return switch (args[0]) {
                // /gc player info [players]
                case "info" -> PlayerData.AllAsUsernames();
                // /gc player [...]
                default -> List.of();
            };
        }
        return List.of();
    }

    // ---------- /gc player info ----------
    private void gcPlayerInfo(@NotNull CommandSender sender, @NotNull String[] args) {
        // validation check
        if (args.length == 0) {
            ChatUtil.SendPrefixedMessage(sender, "§cYou must put the name of the player you want to get info of!§r");
            return;
        }

        PlayerData player = PlayerData.PlayerDataByUsername.getOrDefault(args[0], null);
        if (player == null) {
            ChatUtil.SendPrefixedMessage(sender, "§cPlayer \"§f" + args[0] + "§c\" could not be found!§r");
            return;
        }

        StringBuilder sb = new StringBuilder("\n§6========== PLAYER INFO ==========§r\n");
        sb.append("§a").append(player.Username).append("§r\n");

        // show rank of player in country
        CountryData country = player.getCountry();

        if (player.Rank == PlayerData.PlayerRank.NONE) {
            sb.append("> §Stateless§r");
        }
        else {
            sb.append("> §e").append(player.getRankString()).append("§r of: ").append(country.Name);
        }
        switch (player.Rank) {
            case PlayerData.PlayerRank.LEADER:
                sb.append("> §eLeader of§r: ").append(country.Name);
                break;
            case PlayerData.PlayerRank.CITIZEN:
                sb.append("> §eCitizen of§r: ").append(country.Name);
                break;
            case PlayerData.PlayerRank.NONE:
                sb.append("> §Stateless§r");
                break;
        }
        sb.append("\n");

        sb.append("§6================================§r");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }
}
