package me.rntb.geoCountries.util;

import me.rntb.geoCountries.GeoCountries;
import me.rntb.geoCountries.config.ConfigState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static Path getFilePathFromDataFolder(String fileName, String extension) {
        return Paths.get(GeoCountries.PluginAbsoluteDataFolderPath + File.separator + fileName + "." + extension);
    }

    public static void createPathIfNotExist(Path path, String contents) {
        if(doesPathExist(path))
            return;

        // create path
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            ChatUtil.sendPrefixedLogErrorMessage("Could not create path " + path.toString() + "!");
            return;
        }

        // write to path
        try {
            FileWriter fileWriter = new FileWriter(path.toString());
            fileWriter.write(contents);
            fileWriter.close();
        } catch (IOException e) {
            ChatUtil.sendPrefixedLogErrorMessage("Tried to write to " + path.toString() + " but failed! (IOException)");
            return;
        }

        if (ConfigState.DebugLogging)
            ChatUtil.sendPrefixedLogMessage("Created path " + path.toString() + " and wrote contents!");
    }

    public static boolean doesPathExist(Path path) {
        return Files.exists(path);
    }
}
