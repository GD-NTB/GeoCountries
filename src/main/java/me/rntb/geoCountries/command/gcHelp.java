package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// todo: break into pages
public class gcHelp extends SubCommand {

    public gcHelp(String displayName, String requiredPermission, Boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Lists all commands and gives help for any command.";
        this.HelpPage   = """
                          §f/gc help§a: Lists all commands and a summary of what each does.
                          §f/gc help [subcommands]§a: Displays more information on a specific command, as well as what subcommands it has.""";;
    }

    @Override
    public void doCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        StringBuilder sb = new StringBuilder(ChatUtil.NewlineIfPrefixIsEmpty() +
                                            "§6========== HELP ==========\n");

        // /gc help
        if (args.length == 0) {
            List<SubCommand> subCommands;
            if (sender instanceof Player) {
                subCommands = gc.GetAllowedSubCommands((Player) sender);
            }
            else {
                subCommands = gc.gcSubCommands.values().stream().toList();
            }
            // append help for each command
            for (SubCommand sc : subCommands) {
                sb.append("§f").append(sc.DisplayName).append("§a: ").append(sc.HelpString).append("§f");
                sb.append("\n");
            }
        }

        // /gc help [...]
        else {
            SubCommand sc = gc.gcSubCommands.get(args[0]);
            // if command doesnt exist, escape
            if (sc == null) {
                ChatUtil.SendPrefixedMessage(sender, "§cNo help page found for the command §f/gc " + args[0] + "§c!");
                return;
            }
            // if dont have permission for this command
            if (!sender.hasPermission(sc.RequiredPermission)) {
                ChatUtil.SendPrefixedMessage(sender, "§cYou don't have permission to view help for §f/gc " + args[0] + "§c!");
                return;
            }
            // append help for this command
            sb.append(sc.HelpPage).append("\n");
        }

        sb.append("§6=========================");
        ChatUtil.SendPrefixedMessage(sender, sb.toString());
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            // /gc help 1
            case 1 -> gc.GetAllowedSubCommandsAsStrings((Player) sender);
            default -> List.of();
        };
    }
}
