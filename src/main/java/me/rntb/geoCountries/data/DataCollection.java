package me.rntb.geoCountries.data;

import com.google.gson.Gson;
import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.FileUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// todo: we can have a map of maps, like PlayerProfile.getBy("username").get("AldiyarTynysbaev")
public abstract class DataCollection {

    public static String filePath;
    public static String displayName;

    private static final Gson gson = new Gson();

    static <T> ArrayList<T> readFromFile(String filePath, String displayName, Type typeToken) {
        if (GeoCountries.PluginAbsoluteDataFolderPath == null)
            return null;

        Path path = FileUtil.getFilePathFromDataFolder(filePath, "json");
        FileUtil.createPathIfNotExist(path, "[]");

        try {
            // create reader for file
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            // deserialise from reader
            ArrayList<T> data = gson.fromJson(reader, typeToken);
            reader.close();

            return data != null ? data : new ArrayList<>();

        } catch (IOException e) {
            ChatUtil.sendPrefixedLogErrorMessage("Tried to deserialise/read " + displayName + "from " + String.valueOf(path) + " but failed! (IOException)");
            e.printStackTrace();
            return null;
        }
    }

    static <T> void writeToFile(String filePath, String displayName, List<T> all) {
        if (all == null || GeoCountries.PluginAbsoluteDataFolderPath == null)
            return;

        Path path = FileUtil.getFilePathFromDataFolder(filePath, "json");
        FileUtil.createPathIfNotExist(path, "[]");

        // write to file
        try {
            // create writer for file
            FileWriter writer = new FileWriter(path.toFile());
            // serialise and write
            gson.toJson(all, writer);
            writer.flush();
            writer.close();

            if (ConfigState.debugLogging)
                ChatUtil.sendPrefixedLogMessage("Serialised and wrote " + displayName + " to " + String.valueOf(path) + ".");

        } catch (IOException e) {
            ChatUtil.sendPrefixedLogErrorMessage("Tried to serialise/write " + displayName + " to " + String.valueOf(path) + " but failed! (IOException)");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    // register new datacollection to all
    public static <T> void add(T dataCollection, List<T> all, String displayName) {
        all.add(dataCollection);
        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Added new " + displayName + ".");
    }

    // delete datacollection from all
    // todo: all and displayName should be fields in this class!!
    public static <T> void delete(T dataCollection, List<T> all, String displayName) {
        all.remove(dataCollection);
        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted " + displayName + ".");
    }
}
