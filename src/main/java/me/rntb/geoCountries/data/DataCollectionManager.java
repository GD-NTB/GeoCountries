package me.rntb.geoCountries.data;

public class DataCollectionManager {

    // called in GeoCountries.onEnable()
    public static void init() {
        PlayerProfile.init();
        Country.init();
        CitizenshipApplication.init();
        ClaimChunk.init();
        Faction.init();
        FactionInvite.init();
    }

    public static void save() {
        PlayerProfile.save();
        Country.save();
        CitizenshipApplication.save();
        ClaimChunk.save();
        Faction.save();
        FactionInvite.save();
    }

    public static void purgeAll() {
        PlayerProfile.purge();
        Country.purge();
        CitizenshipApplication.purge();
        ClaimChunk.purge();
        Faction.purge();
        FactionInvite.purge();
    }
}
