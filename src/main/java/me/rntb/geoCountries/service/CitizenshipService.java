package me.rntb.geoCountries.service;

import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.data.PlayerProfile.PlayerRank;

public class CitizenshipService {

    public static void joinCountry(PlayerProfile player, Country country) {
        leaveCountry(player);

        // add to new country
        country.citizens.add(player.uuid);

        player.citizenship = country.uuid;
        player.rank = PlayerRank.CITIZEN;
    }

    public static void leaveCountry(PlayerProfile player) {
        Country currentCountry = player.getCitizenship();
        if (currentCountry == null)
            return;
        currentCountry.citizens.remove(player.uuid);

        if (player.uuid.equals(currentCountry.leader))
            currentCountry.leader = null;

        player.citizenship = null;
        player.rank = PlayerRank.NONE;
    }
}
