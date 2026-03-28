package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcCitizenship extends GeoCommand {

    public gcCitizenship(String name, String requiredPermission, ItemStack menuButtonItem) {
        super(name, requiredPermission, menuButtonItem);
        this.helpString = "Manages your citizenship and your citizens.";
        addChild(new gcCitizenshipApply("apply", "gc.citizenship.apply", ItemStack.of(Material.WRITABLE_BOOK)));
        addChild(new gcCitizenshipRenounce("renounce", "gc.citizenship.renounce", ItemStack.of(Material.DARK_OAK_DOOR)));
        addChild(new gcCitizenshipRevoke("revoke", "gc.citizenship.revoke", ItemStack.of(Material.GRINDSTONE)));
        addChild(new gcCitizenshipReceived("received", "gc.citizenship.received", ItemStack.of(Material.CHEST)));
        addChild(new gcCitizenshipSent("sent", "gc.citizenship.sent", ItemStack.of(Material.BOOK)));
        addChild(new gcCitizenshipUnapply("unapply", "gc.citizenship.unapply", ItemStack.of(Material.CARROT_ON_A_STICK)));
        addChild(new gcCitizenshipAccept("accept", "gc.citizenship.accept", null));
        addChild(new gcCitizenshipReject("reject", "gc.citizenship.reject", null));
        addAlias("citizen");
    }
}
