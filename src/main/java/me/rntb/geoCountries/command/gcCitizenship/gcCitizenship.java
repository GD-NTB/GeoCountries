package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class gcCitizenship extends GeoCommand {

    public gcCitizenship(GeoCommand parentCommand, String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(parentCommand, name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages your citizenship and your citizens.";
        this.childCommands.put("apply", new gcCitizenshipApply(this, "apply", "/gc citizenship apply", "gc.citizenship.apply", ItemStack.of(Material.WRITABLE_BOOK)));
        this.childCommands.put("renounce", new gcCitizenshipRenounce(this, "renounce", "/gc citizenship renounce", "gc.citizenship.renounce", ItemStack.of(Material.SHEARS)));
        this.childCommands.put("revoke", new gcCitizenshipRevoke(this, "revoke", "/gc citizenship revoke", "gc.citizenship.revoke", ItemStack.of(Material.GRINDSTONE)));
        this.childCommands.put("received", new gcCitizenshipReceived(this, "received", "/gc citizenship received", "gc.citizenship.received", ItemStack.of(Material.CHEST)));
        this.childCommands.put("sent", new gcCitizenshipSent(this, "sent", "/gc citizenship sent", "gc.citizenship.sent", ItemStack.of(Material.BOOK)));
        this.childCommands.put("unapply", new gcCitizenshipUnapply(this, "unapply", "/gc citizenship unapply", "gc.citizenship.unapply", ItemStack.of(Material.CARROT_ON_A_STICK)));
        this.childCommands.put("accept", new gcCitizenshipAccept(this, "accept", "/gc citizenship accept", "gc.citizenship.accept", null));
        this.childCommands.put("reject", new gcCitizenshipReject(this, "reject", "/gc citizenship reject", "gc.citizenship.reject", null));
    }
}
