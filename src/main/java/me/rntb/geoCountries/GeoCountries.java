package me.rntb.geoCountries;

import me.rntb.geoCountries.command.gc;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.listener.ChatListener;
import me.rntb.geoCountries.listener.InventoryListener;
import me.rntb.geoCountries.listener.JoinListener;
import me.rntb.geoCountries.listener.LeaveListener;
import me.rntb.geoCountries.util.ChatUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Objects;

// todo: gui
// todo: change colour of settings [edit] and [default]
// todo: a lot of commands need to be support no args, e.g. /gc country create should prompt a Response
// todo: rewrite helper functions that dont need to be List to be type of Array
// todo: in-country ranks
// todo: promote command
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
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        // initialise commands
        Objects.requireNonNull(getCommand("gc")).setExecutor(new gc("gc", "/gc", null, null));

        // initialise globals
        PluginName = getPluginMeta().getName();
        PluginVersion = getPluginMeta().getVersion();
        PluginNameAndVersion = PluginName + " [" + PluginVersion + "]";
        PluginAbsoluteDataFolderPath = getDataPath().toAbsolutePath();

        // initialise data collections
        DataCollectionManager.init();

        ChatUtil.sendPrefixedLogMessage("Plugin enabled!");

        // set up bstats
        bStatsSetup();
    }

    @Override
    public void onDisable() {
        // save data collections
        DataCollectionManager.save();

        ChatUtil.sendPrefixedLogMessage("Plugin disabled!");
    }

    private void bStatsSetup() {
        int pluginId = 29384;
        new Metrics(this, pluginId);
    }
}
