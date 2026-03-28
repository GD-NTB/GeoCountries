package me.rntb.geoCountries.command;

import me.rntb.geoCountries.type.PageNumberAndArgs;
import me.rntb.geoCountries.type.Pagination;
import me.rntb.geoCountries.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// todo: components to be able to click on a command to see its help
public class gcHelp extends GeoCommand {

    public gcHelp(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        setHelpString("Lists all info about all/any commands.");
    }

    private static final int ENTRIES_PER_PAGE = 15;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        TextComponent.Builder message = Component.text();

        message.append(ChatUtil.newlineIfPrefixIsEmptyComponent())
               .append(Component.text("§6========== HELP =========="))
               .append(Component.newline());

        // parse args
        PageNumberAndArgs pageNumberAndArgs = PageNumberAndArgs.parse(args);
        int wantedPage = pageNumberAndArgs.pageNumber();
        String[] commandNameArgs = pageNumberAndArgs.args();

        // get command name
        String fullCommandName = GeoCommand.baseCommand.getCommandString();
        String commandName = null;
        // if command specified, append it to fullCommandName
        if (commandNameArgs != null) {
            commandName = String.join(" ", pageNumberAndArgs.args());
            fullCommandName += " " + commandName;
        }

        // pagination fields
        int pageIndex = 0, pageCount = 0;

        // get command
        Pair<GeoCommand, String[]> commandPair = GeoCommand.get(fullCommandName);
        GeoCommand command = commandPair.getLeft();
        if (command == null)
            message.append(Component.text("§cCommand §f" + fullCommandName + "§c could not be found!"));
        else {
            String helpPage = command.getHelpPage(sender);
            if (helpPage == null) // no permission
                helpPage = "§cCommand §f" + fullCommandName + "§c could not be found!";

            // calculate required page of helpPage
            Pagination pagination = Pagination.paginate(helpPage, "\n", wantedPage, ENTRIES_PER_PAGE);
            String paginatedHelpPage = (String) pagination.content();
            pageIndex = pagination.pageIndex();
            pageCount = pagination.pageCount();

            // append
            message.append(Component.text(paginatedHelpPage));
        }

        // append footer
        message.append(Component.newline())
               .append(Component.text("§6========================="))
               .append(Component.newline())
               .append(Component.text("      "))
               .append(ChatUtil.getPaginationButtons("gc help " + (pageIndex - 1) + (commandName != null ? " " + commandName : ""),
                                                     "gc help " + (pageIndex + 1) + (commandName != null ? " " + commandName : ""),
                                                     pageIndex, pageCount));

        ChatUtil.sendPrefixedMessage(sender, message.build());
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        String commandName = gc.baseCommand.getCommandString();
        if (args.length != 0)
            commandName += " " + String.join(" ", args);

        Pair<GeoCommand, String[]> commandPair = GeoCommand.get(commandName);
        GeoCommand command = commandPair.getLeft();
        if (command == null)
            return List.of();

        return command.allowedChildCommandsAsStrings(sender);
    }
}
