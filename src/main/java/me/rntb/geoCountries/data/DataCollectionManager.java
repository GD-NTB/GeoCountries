package me.rntb.geoCountries.data;

public class DataCollectionManager {

    // called in GeoCountries.onEnable()
    public static void init() {
        PlayerProfile.init();
        Country.init();
        CitizenshipApplication.init();
    }

    public static void save() {
        PlayerProfile.save();
        Country.save();
        CitizenshipApplication.save();
    }
}
