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

public abstract class DataCollection {

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
            ChatUtil.sendPrefixedLogErrorMessage("Tried to deserialise/read " + displayName + "from " + path.toString() + " but failed! (IOException)");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    static <T> void writeToFile(String filePath, String displayName, List<T> all) {
        if (GeoCountries.PluginAbsoluteDataFolderPath == null)
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

//            if (ConfigState.DebugLogging)
//                ChatUtil.sendPrefixedLogMessage("Serialised and wrote " + displayName + " to " + path.toString() + ".");

        } catch (IOException e) {
            ChatUtil.sendPrefixedLogErrorMessage("Tried to serialise/write " + displayName + " to " + path.toString() + " but failed! (IOException)");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    // register new datacollection to all
    static <T> void addNew(T newDataCollection, List<T> all, String displayName) {
        all.add(newDataCollection);
        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Added new " + displayName + ".");
    }

    // delete datacollection from all
    static <T> void delete(T dataCollection, List<T> all, String displayName) {
        all.remove(dataCollection);
        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Deleted " + displayName + ".");
    }
}
