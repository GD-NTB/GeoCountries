package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class gcHelp extends SubCommand {

    public gcHelp(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Lists all commands and gives help for any command.";
        this.HelpPage   = """
                          §f/gc help: §aLists all commands and a summary of what each does.
                          §f/gc help [subcommands]: §aDisplays more information on a specific command, as well as what subcommands it has.""";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== HELP =========="))
               .append(Component.newline());

        int effectiveIndex = 0, pageCount = 0;
        String commandForPrevious, commandForNext;

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

        message.append(Component.text("§6========================="))
               .append(Component.newline())
               .append(Component.text("      "));

        // append chat page control buttons
        message.append(ChatUtil.chatPageControlButtons(commandForPrevious, commandForNext, effectiveIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    private static Pagination getHelpAll(CommandSender sender, int index) {
        StringBuilder sb = new StringBuilder();
        List<SubCommand> subCommands;
        if (sender instanceof Player player)
            subCommands = gc.allowedSubCommands(player);
        else
            subCommands = gc.subCommands.values().stream().toList();

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
        // replace with alias if needed
        String subCommandNameAlias = gc.subCommandsAliases.get(commandName);
        if (subCommandNameAlias != null)
            commandName = subCommandNameAlias;
        SubCommand sc = gc.subCommands.get(commandName);
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
        return args.length == 1 ? gc.subCommandsTabAutoCompleteList(sender) : List.of();
    }
}
