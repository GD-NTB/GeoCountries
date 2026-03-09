package me.rntb.geoCountries.service;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;

public class CitizenshipService {

    public static void joinCountry(PlayerProfile player, Country country) {
        leaveCountry(player);

        // add to new country
        country.getCitizens().add(player.getUUID());

        player.setCitizenshipInternal(country.getUUID());
        player.setPositionInternal(Position.CITIZEN);

        // remove all pending citizenship applications
        CitizenshipApplicationService.deleteAllSentByApplicant(player);
    }

    public static void leaveCountry(PlayerProfile player) {
        Country currentCountry = player.getCitizenshipCountry();
        if (currentCountry == null)
            return;

        if (player.getUUID().equals(currentCountry.getLeader()))
            PositionService.demoteFromLeader(player);

        currentCountry.getCitizens().remove(player.getUUID());

        player.setCitizenshipInternal(null);
        player.setPositionInternal(Position.NONE);
    }
}
