package me.rntb.geoCountries;

import me.rntb.geoCountries.command.gcCommand;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.listener.JoinListener;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

// todo: config (chat util /prefix)
// todo: request citizenship
// todo: grant citizenship
// todo: renounce citizenship
// todo: promote command
// todo: if leader renounces citizenship, next in command should inherit?
// todo: update command (usernames)
// todo: subcommand autocomplete only for allowed commands
// todo: claiming chunks (max chunks in config)
public class GeoCountries extends JavaPlugin {

    public static String PluginName;
    public static String PluginVersion;
    public static String PluginNameAndVersion;
    public static Path PluginAbsoluteDataFolderPath;

    public static JavaPlugin self; // instance

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        self = this;

        // register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        // initialise commands
        getCommand("gc").setExecutor(new gcCommand());
        gcCommand.registerSubCommands();

        // initialise globals
        PluginName = this.getDescription().getName();
        PluginVersion = this.getDescription().getVersion();
        PluginNameAndVersion = PluginName + " [" + PluginVersion + "]";
        PluginAbsoluteDataFolderPath = this.getDataPath().toAbsolutePath();

        // initialise data collections
        DataCollectionManager.CollectionsInit();

        ChatUtil.SendPrefixedLogMessage("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // save data collections
        DataCollectionManager.SaveCollections();

        ChatUtil.SendPrefixedLogMessage("Plugin disabled!");
    }
}
