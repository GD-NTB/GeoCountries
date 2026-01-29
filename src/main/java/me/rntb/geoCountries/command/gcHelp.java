package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class gcHelp extends SubCommand {

    public gcHelp(String displayName, String requiredPermission, boolean consoleCanUse) {
        super(displayName, requiredPermission, consoleCanUse);
        this.HelpString = "Lists all commands and gives help for any command.";
        this.HelpPage   = """
                          §f/gc help: §aLists all commands and a summary of what each does.
                          §f/gc help [subcommands]: §aDisplays more information on a specific command, as well as what subcommands it has.""";;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        TextComponent.Builder message = Component.text();
        MiniMessage mm = MiniMessage.miniMessage();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(mm.deserialize("<gold>========== HELP =========="))
               .append(Component.newline());

        int effectiveIndex = 0;
        int pageCount = 0;
        String commandForPrevious = "";
        String commandForNext = "";

        // /gc help
        if (args.length == 0) {
            Pagination page = getHelpAll(sender, 0);
            message.append(Component.text(page.text))
                   .append(Component.newline());
            pageCount = page.pageCount;
            effectiveIndex = page.index;
            commandForPrevious = "/gc help 1";
            commandForNext = "/gc help 2";
        }
        // /gc help [page/command] [...]
        else {
            // /gc help [page/command]
            if (args.length == 1) {
                // /gc help [page]
                try {
                    Pagination page = getHelpAll(sender, Integer.parseInt(args[0])); // won't return null cos page is clamped 0 to max
                    message.append(Component.text(page.text))
                           .append(Component.newline());
                    pageCount = page.pageCount;
                    effectiveIndex = page.index;
                    commandForPrevious = "/gc help " + (effectiveIndex-1);
                    commandForNext = "/gc help " + (effectiveIndex+1);
                }
                // /gc help [command]
                catch (NumberFormatException e) {
                    Pagination page = getHelpSpecific(sender, args[0], 0);
                    // if command=null or no permission
                    if (page == null)
                        message.append(Component.text("§cNo help page found for the command §f/gc " + args[0] + "§c!"))
                               .append(Component.newline());
                    else {
                        message.append(Component.text(page.text))
                               .append(Component.newline());
                        pageCount = page.pageCount;
                        effectiveIndex = page.index;
                    }
                    commandForPrevious = "/gc help " + args[0] + " 1";
                    commandForNext = "/gc help " + args[0] + " 2";
                }
            }
            // /gc help [page/command] [page?]
            else {
                int index = 0;
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) { }
                Pagination page = getHelpSpecific(sender, args[0], index);
                if (page == null) // if command=null or no permission
                    message.append(Component.text("§cNo help page found for the command §f/gc " + args[0] + "§c!"))
                           .append(Component.newline());
                else {
                    message.append(Component.text(page.text))
                           .append(Component.newline());
                    pageCount = page.pageCount;
                    effectiveIndex = page.index;
                }
                commandForPrevious = "/gc help " + args[0] + " " + (effectiveIndex-1);
                commandForNext = "/gc help " + args[0] + " " + (effectiveIndex+1);
            }
        }

        message.append(mm.deserialize("<gold>========================="))
                .append(Component.newline())
                // [<<<] button
                .append(mm.deserialize("<click:run_command:'" + commandForPrevious + "'>" +
                                      "<hover:show_text:'<dark_gray>Click to go to previous page.</dark_gray>'>" +
                                      "<dark_gray><bold>[<<<]</bold></dark_gray>" +
                                      "</hover></click>"))
               .append(Component.text("  "))
               // (page/pages) text
               .append(Component.text("§8(%d/%d)"
                                             .formatted(effectiveIndex, pageCount)))
               .append(Component.text("  "))
               // [>>>] button
               .append(mm.deserialize("<click:run_command:'" + commandForNext + "'>" +
                                      "<hover:show_text:'<dark_gray>Click to go to next page.</dark_gray>'>" +
                                      "<dark_gray><bold>[>>>]</bold></dark_gray>" +
                                      "</hover></click>"));
        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static Pagination getHelpAll(CommandSender sender, int index) {
        StringBuilder sb = new StringBuilder();
        List<SubCommand> subCommands;
        if (sender instanceof Player player) {
            subCommands = gc.GetAllowedSubCommands(player);
        }
        else {
            subCommands = gc.gcSubCommands.values().stream().toList();
        }
        // append help for each command
        for (SubCommand sc : subCommands) {
            sb.append("§f").append(sc.DisplayName).append(": §a").append(sc.HelpString).append("§f");
            sb.append("\n");
        }

        // split into pages and return
        return Pagination.paginate(String.valueOf(sb), "\n", index, 10);
    }

    private static Pagination getHelpSpecific(CommandSender sender, String commandName, int index) {
        StringBuilder sb = new StringBuilder();
        SubCommand sc = gc.gcSubCommands.get(commandName);
        // if command doesnt exist or no permission, escape
        if (sc == null || !sender.hasPermission(sc.RequiredPermission)) {
            return null;
        }
        // append help for this command
        sb.append(sc.HelpPage).append("\n");

        // split into pages and return
        return Pagination.paginate(String.valueOf(sb), "\n", index, 10);
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
