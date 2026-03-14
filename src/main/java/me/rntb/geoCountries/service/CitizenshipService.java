package me.rntb.geoCountries.service;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.Position;

public class CitizenshipService {

    public static void joinCountry(PlayerProfile playerProfile, Country country) {
        leaveCountry(playerProfile);

        // add to new country
        country.getCitizens().add(playerProfile.getUUID());

        playerProfile.setCitizenshipInternal(country.getUUID());
        playerProfile.setPositionInternal(Position.CITIZEN);

        // remove all pending citizenship applications
        CitizenshipApplicationService.deleteAllSentByApplicant(playerProfile);
    }

    public static void leaveCountry(PlayerProfile playerProfile) {
        Country currentCountry = playerProfile.getCitizenshipCountry();
        if (currentCountry == null)
            return;

        if (playerProfile.getUUID().equals(currentCountry.getLeader()))
            PositionService.demoteFromLeader(playerProfile);

        currentCountry.getCitizens().remove(playerProfile.getUUID());

        playerProfile.setCitizenshipInternal(null);
        playerProfile.setPositionInternal(Position.NONE);
    }
}
