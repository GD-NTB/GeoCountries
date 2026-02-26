package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.GeoCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcCitizenship extends GeoCommand {

    public gcCitizenship(String name, String displayName, String requiredPermission, Material menuButtonItem) {
        super(name, displayName, requiredPermission, menuButtonItem);
        this.helpString = "Manages your citizenship and your citizens.";
        this.childCommands = new LinkedHashMap<>() {{
            put("accept", new gcCitizenshipAccept("accept", "/gc citizenship accept", "gc.citizenship.accept", Material.LIME_WOOL));
            put("apply", new gcCitizenshipApply("apply", "/gc citizenship apply", "gc.citizenship.apply", Material.WRITABLE_BOOK));
            put("received", new gcCitizenshipReceived("received", "/gc citizenship received", "gc.citizenship.received", Material.CHEST));
            put("reject", new gcCitizenshipReject("reject", "/gc citizenship reject", "gc.citizenship.reject", Material.RED_WOOL));
            put("renounce", new gcCitizenshipRenounce("renounce", "/gc citizenship renounce", "gc.citizenship.renounce", Material.SHEARS));
            put("revoke", new gcCitizenshipRevoke("revoke", "/gc citizenship revoke", "gc.citizenship.revoke", Material.GRINDSTONE));
            put("sent", new gcCitizenshipSent("sent", "/gc citizenship sent", "gc.citizenship.sent", Material.BOOK));
            put("unsend", new gcCitizenshipUnsend("unsend", "/gc citizenship unsend", "gc.citizenship.unsend", Material.WIND_CHARGE));
        }};
    }
}
