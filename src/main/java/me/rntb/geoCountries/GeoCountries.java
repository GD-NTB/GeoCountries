package me.rntb.geoCountries;

import me.rntb.geoCountries.command.gc;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.listener.JoinListener;
import me.rntb.geoCountries.listener.LeaveListener;
import me.rntb.geoCountries.listener.ChatListener;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.util.ChatUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

// todo: sent citizenship applications list
// todo: received citizenship application list
// todo: accept citizenship
// todo: you can only apply to one country at a time! make it not like that!!!
// todo: sent citizenship application limit (config specified)
// todo: auto accept citizenship applications (/gc country settings [...])/
// todo: applications should expire after a config defined amount of time
// todo: renounce citizenship
// todo: promote command
// todo: if leader loses citizenship, next in command should inherit?
// todo: update command (usernames)
// todo: claiming (max chunks in config)
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

        // config
        ConfigManager.init();

        // register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        // initialise commands
        getCommand("gc").setExecutor(new gc());
        gc.registerSubCommands();

        // initialise globals
        PluginName = this.getDescription().getName();
        PluginVersion = this.getDescription().getVersion();
        PluginNameAndVersion = PluginName + " [" + PluginVersion + "]";
        PluginAbsoluteDataFolderPath = this.getDataPath().toAbsolutePath();

        // initialise data collections
        DataCollectionManager.init();

        ChatUtil.sendPrefixedLogMessage("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // save data collections
        DataCollectionManager.save();

        ChatUtil.sendPrefixedLogMessage("Plugin disabled!");
    }
}
