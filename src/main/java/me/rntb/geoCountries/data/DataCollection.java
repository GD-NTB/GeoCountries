package me.rntb.geoCountries.data;

import com.google.gson.Gson;
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

    private static final Gson GSON = new Gson();

    static <T> ArrayList<T> ReadFromFile(String filePath, String displayName, Type typeToken) {
        Path path = FileUtil.GetFilePathFromDataFolder(filePath, "json");
        FileUtil.CreatePathIfNotExist(path, "[]");

        try {
            // create reader for file
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            // deserialise from reader
            ArrayList<T> data = GSON.fromJson(reader, typeToken);
            reader.close();

            ChatUtil.SendPrefixedLogMessage("Read " + displayName + " from " + path.toString() + " and deserialised.");

            return data != null ? data : new ArrayList<>();

        } catch (IOException e) {
            ChatUtil.SendPrefixedLogErrorMessage("Tried to deserialise/read " + displayName + "from " + path.toString() + " but failed! (IOException)");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    static <T> void WriteToFile(String filePath, String displayName, List<T> all) {
        Path path = FileUtil.GetFilePathFromDataFolder(filePath, "json");
        FileUtil.CreatePathIfNotExist(path, "[]");

        // write to file
        try {
            // create writer for file
            FileWriter writer = new FileWriter(path.toFile());
            // serialise and write
            GSON.toJson(all, writer);
            writer.flush();
            writer.close();

            ChatUtil.SendPrefixedLogMessage("Serialised and wrote " + displayName + " to " + path.toString() + ".");

        } catch (IOException e) {
            ChatUtil.SendPrefixedLogErrorMessage("Tried to serialise/write " + displayName + " to " + path.toString() + " but failed! (IOException)");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    // register new datacollection to All
    static <T> void AddNew(T newDataCollection, List<T> all, String displayName) {
        all.add(newDataCollection);
        ChatUtil.SendPrefixedLogMessage("Added new " + displayName + ".");
    }

    // delete datacollection from All
    static <T> void Delete(T dataCollection, List<T> all, String displayName) {
        all.remove(dataCollection);
        ChatUtil.SendPrefixedLogMessage("Deleted " + displayName + ".");
    }
}
