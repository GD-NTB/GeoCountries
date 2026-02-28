package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class gcCitizenship extends GeoCommand {

    public gcCitizenship(String name, String displayName, String requiredPermission, ItemStack menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages your citizenship and your citizens.";
        this.childCommands = new LinkedHashMap<>() {{
            put("apply", new gcCitizenshipApply("apply", "/gc citizenship apply", "gc.citizenship.apply", ItemStack.of(Material.WRITABLE_BOOK)));
            put("renounce", new gcCitizenshipRenounce("renounce", "/gc citizenship renounce", "gc.citizenship.renounce", ItemStack.of(Material.SHEARS)));
            put("revoke", new gcCitizenshipRevoke("revoke", "/gc citizenship revoke", "gc.citizenship.revoke", ItemStack.of(Material.GRINDSTONE)));
            put("accept", new gcCitizenshipAccept("accept", "/gc citizenship accept", "gc.citizenship.accept", null));
            put("reject", new gcCitizenshipReject("reject", "/gc citizenship reject", "gc.citizenship.reject", null));
            put("received", new gcCitizenshipReceived("received", "/gc citizenship received", "gc.citizenship.received", ItemStack.of(Material.CHEST)));
            put("sent", new gcCitizenshipSent("sent", "/gc citizenship sent", "gc.citizenship.sent", ItemStack.of(Material.BOOK)));
            put("unsend", new gcCitizenshipUnsend("unsend", "/gc citizenship unsend", "gc.citizenship.unsend", ItemStack.of(Material.CARROT_ON_A_STICK)));
        }};
    }
}
