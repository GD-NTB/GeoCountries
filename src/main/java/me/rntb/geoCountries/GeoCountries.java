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
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

// todo: gui
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
        getCommand("gc").setExecutor(new gc());

        // initialise globals
        PluginName = this.getDescription().getName();
        PluginVersion = this.getDescription().getVersion();
        PluginNameAndVersion = PluginName + " [" + PluginVersion + "]";
        PluginAbsoluteDataFolderPath = this.getDataPath().toAbsolutePath();

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
        // You can find the plugin id of your plugins on
        // the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 29384;
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(
                new SimplePie("chart_id", () -> "My value")
        );
    }
}
