package me.rntb.geoCountries.command.gcCitizenship;

import me.rntb.geoCountries.command.SubCommand;
import org.bukkit.Material;

import java.util.Map;

public class gcCitizenship extends SubCommand {

    public gcCitizenship(String name, String displayName, String requiredPermission, boolean consoleCanUse, Material menuMaterialItem) {
        super(name, displayName, requiredPermission, consoleCanUse, menuMaterialItem);
        this.HelpString = "Manages your citizenship and your citizens.";
        this.subSubCommands = Map.ofEntries(
                Map.entry("accept", new gcCitizenshipAccept("accept", "/gc citizenship accept", "gc.citizenship.accept")),
                Map.entry("apply", new gcCitizenshipApply("apply", "/gc citizenship apply", "gc.citizenship.apply")),
                Map.entry("received", new gcCitizenshipReceived("received", "/gc citizenship received", "gc.citizenship.received")),
                Map.entry("reject", new gcCitizenshipReject("reject", "/gc citizenship reject", "gc.citizenship.reject")),
                Map.entry("renounce", new gcCitizenshipRenounce("renounce", "/gc citizenship renounce", "gc.citizenship.renounce")),
                Map.entry("revoke", new gcCitizenshipRevoke("revoke", "/gc citizenship revoke", "gc.citizenship.revoke")),
                Map.entry("sent", new gcCitizenshipSent("sent", "/gc citizenship sent", "gc.citizenship.sent")),
                Map.entry("unsend", new gcCitizenshipUnsend("unsend", "/gc citizenship unsend", "gc.citizenship.unsend"))
        );
    }
}
