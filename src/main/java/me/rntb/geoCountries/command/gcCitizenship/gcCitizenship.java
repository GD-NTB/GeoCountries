package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public class gcCitizenship extends SubCommand {

    public gcCitizenship(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages your citizenship and your citizens.";
        this.subSubCommands = new LinkedHashMap<>() {{
            put("accept", new gcCitizenshipAccept("accept", "/gc citizenship accept", "gc.citizenship.accept"));
            put("apply", new gcCitizenshipApply("apply", "/gc citizenship apply", "gc.citizenship.apply"));
            put("received", new gcCitizenshipReceived("received", "/gc citizenship received", "gc.citizenship.received"));
            put("reject", new gcCitizenshipReject("reject", "/gc citizenship reject", "gc.citizenship.reject"));
            put("renounce", new gcCitizenshipRenounce("renounce", "/gc citizenship renounce", "gc.citizenship.renounce"));
            put("revoke", new gcCitizenshipRevoke("revoke", "/gc citizenship revoke", "gc.citizenship.revoke"));
            put("sent", new gcCitizenshipSent("sent", "/gc citizenship sent", "gc.citizenship.sent"));
            put("unsend", new gcCitizenshipUnsend("unsend", "/gc citizenship unsend", "gc.citizenship.unsend"));
        }};
    }
}
