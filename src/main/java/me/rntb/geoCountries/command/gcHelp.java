package me.rntb.geoCountries.command;

import me.rntb.geoCountries.types.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// todo: rewrite using new Command.getHelpPage method
public class gcHelp extends GeoCommand {

    public gcHelp(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Lists all commands or displays a specific command's info.";
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

    private Pagination getHelpAll(CommandSender sender, int index) {
        StringBuilder sb = new StringBuilder();
        List<GeoCommand> childCommands;
        if (sender instanceof Player player)
            childCommands = GeoCommand.baseCommand.allowedChildCommands(player);
        else
            childCommands = GeoCommand.baseCommand.childCommands.values().stream().toList();

        // append help for each command
        for (GeoCommand command : childCommands) {
            sb.append("§f%s: §a%s§f\n"
                      .formatted(command.displayName, command.helpString));
        }

        // split into pages and return
        return Pagination.paginate(String.valueOf(sb), "\n", index, 10);
    }

    private Pagination getHelpSpecific(CommandSender sender, String commandName, int index) {
        StringBuilder sb = new StringBuilder();
        // replace with alias if needed
        String commandAlias = ((gc) GeoCommand.baseCommand).childCommandsAliases.get(commandName);
        if (commandAlias != null)
            commandName = commandAlias;
        GeoCommand command = GeoCommand.baseCommand.childCommands.get(commandName);
        // if command doesnt exist or no permission, escape
        if (command == null || !sender.hasPermission(command.permission))
            return null;
        // append help for this command
        sb.append(command.getHelpPage()).append("\n");

        // split into pages and return
        return Pagination.paginate(String.valueOf(sb), "\n", index, 10);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return args.length == 1 ? GeoCommand.baseCommand.getTabCompletion(sender, new String[] { }) : List.of();
    }
}
