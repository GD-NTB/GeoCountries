package me.rntb.geoCountries.command;

import me.rntb.geoCountries.metadata.PlayerMetadata;
import me.rntb.geoCountries.data.CitizenshipApplication;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.UuidUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class gcDump extends GeoCommand {

    public gcDump(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Prints some debug shite in the chat for debugging.";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ChatUtil.sendPrefixedMessage(sender, """
                                             %sPlayerProfile.all(%s)
                                             ----------
                                             CitizenshipApplication.sentByApplicant(%s)
                                             ----------
                                             Country.all(%s)
                                             ----------
                                             Player.isMenuOpen(%s)
                                             """
                                             .formatted(ChatUtil.newlineIfPrefixIsEmpty(),
                                                        PlayerProfile.all.size(),
                                                        CitizenshipApplication.sentByApplicant.size(),
                                                        Country.all.size(),
                                                        PlayerMetadata.isMenuOpen.get(UuidUtil.getUUIDOfCommandSender(sender))));
    }
}
