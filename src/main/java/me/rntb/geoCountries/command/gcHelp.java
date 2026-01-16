package me.rntb.geoCountries.command;

import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// todo: break into pages
public class gcHelp extends SubCommand {

    public gcHelp(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Lists all commands and gives help for any command.";
        this.HelpPage   = """
                          §f/gc help: §aLists all commands and a summary of what each does.
                          §f/gc help [subcommands]: §aDisplays more information on a specific command, as well as what subcommands it has.""";;
    }

    @Override
    public void doCommand(CommandSender sender,  String[] args) {
        StringBuilder sb = new StringBuilder(ChatUtil.newlineIfPrefixIsEmpty() +
                                            "§6========== HELP ==========\n");
        // /gc help
        if (args.length == 0) {
            List<SubCommand> subCommands;
            if (sender instanceof Player player) {
                subCommands = gc.GetAllowedSubCommands(player);
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
                ChatUtil.sendPrefixedMessage(sender, "§cNo help page found for the command §f/gc " + args[0] + "§c!");
                return;
            }
            // if dont have permission for this command
            if (!sender.hasPermission(sc.RequiredPermission)) {
                ChatUtil.sendPrefixedMessage(sender, "§cYou don't have permission to view help for §f/gc " + args[0] + "§c!");
                return;
            }
            // append help for this command
            sb.append(sc.HelpPage).append("\n");
        }

        sb.append("§6=========================");
        ChatUtil.sendPrefixedMessage(sender, sb.toString());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender,  String[] args) {
        Player player = (Player) sender;
        return switch (args.length) {
            // /gc help 1
            case 1 -> gc.GetAllowedSubCommandsAsStrings(player);
            default -> List.of();
        };
    }
}
