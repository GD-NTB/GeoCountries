package me.rntb.geoCountries.util;

import me.rntb.geoCountries.GeoCountries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static Path GetFilePathFromDataFolder(String fileName, String extension) {
        // set in GeoCountries.onEnable
        return Paths.get(GeoCountries.PluginAbsoluteDataFolderPath + File.separator + fileName + "." + extension);
    }

    public static void CreatePathIfNotExist(Path path, String contents) {
        if(DoesPathExist(path)) { return; }

        // create path
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            ChatUtil.SendPrefixedLogErrorMessage("Could not create path " + path.toString() + "!");
            return;
        }

        // write to path
        try {
            FileWriter fileWriter = new FileWriter(path.toString());
            fileWriter.write(contents);
            fileWriter.close();
        } catch (IOException e) {
            ChatUtil.SendPrefixedLogErrorMessage("Tried to write to " + path.toString() + " but failed! (IOException)");
            return;
        }

        ChatUtil.SendPrefixedLogMessage("Created path " + path.toString() + " and wrote contents!");
    }

    public static Boolean DoesPathExist(Path path) {
        return Files.exists(path);
    }
}
