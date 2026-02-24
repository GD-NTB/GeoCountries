package me.rntb.geoCountries.command;

import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.types.MenuPage;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class gcDump extends SubCommand {

    public gcDump(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Prints some debug shite in the chat for debugging.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, """
                                             %sPlayerProfile.all(%s)
                                             byUsername(%s), byUUID(%s)
                                             ----------
                                             CitizenshipApplication.sentByApplicant(%s)
                                             ----------
                                             Country.all(%s)
                                             ----------
                                             Player.isMenuOpen(%s)
                                             """
                                             .formatted(ChatUtil.newlineIfPrefixIsEmpty(),
                                                        PlayerProfile.all.size(),
                                                        PlayerProfile.byUsername.size(), PlayerProfile.byUUID.size(),
                                                        CitizenshipApplication.sentByApplicant.size(),
                                                        Country.all.size(),
                                                        ((Player) sender).getPersistentDataContainer().has(MenuPage.ISMENUOPEN_KEY, PersistentDataType.BOOLEAN)));
    }
}
