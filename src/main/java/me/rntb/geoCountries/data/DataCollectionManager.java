package me.rntb.geoCountries.data;

public class DataCollectionManager {

    // called in GeoCountries.onEnable()
    public static void init() {
        PlayerProfile.init();
        Country.init();
        CitizenshipApplication.init();
        ClaimChunk.init();
    }

    public static void save() {
        PlayerProfile.save();
        Country.save();
        CitizenshipApplication.save();
        ClaimChunk.save();
    }

    public static void purgeAll() {
        PlayerProfile.purge();
        Country.purge();
        CitizenshipApplication.purge();
        ClaimChunk.purge();
    }
}
