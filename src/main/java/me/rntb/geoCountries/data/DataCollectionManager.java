package me.rntb.geoCountries.data;

public class DataCollectionManager {

    // called in GeoCountries.onEnable()
    public static void CollectionsInit() {
        PlayerData.Init();
        CountryData.Init();
    }

    public static void SaveCollections() {
        PlayerData.Save();
        CountryData.Save();
    }
}
