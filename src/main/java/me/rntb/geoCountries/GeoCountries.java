package me.rntb.geoCountries;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gc;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.listener.ChatListener;
import me.rntb.geoCountries.listener.InventoryListener;
import me.rntb.geoCountries.listener.JoinListener;
import me.rntb.geoCountries.listener.LeaveListener;
import me.rntb.geoCountries.util.ChatUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Objects;

// todo: getters setters
// todo: claiming
// todo: in-country ranks
// todo: promote command
public class GeoCountries extends JavaPlugin {

    public static JavaPlugin self; // plugin instance

    public static Path pluginAbsoluteDataFolderPath;

    // shorthand
    public static Server server;
    public static PluginManager pluginManager;
    public static PluginMeta pluginMeta;

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        self = this;

        pluginAbsoluteDataFolderPath = getDataPath().toAbsolutePath();

        server = getServer();
        pluginManager = server.getPluginManager();
        pluginMeta = getPluginMeta();

        // config
        ConfigManager.init();

        // register listeners
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new LeaveListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);

        // initialise base command
        GeoCommand.baseCommand = new gc("gc", "/gc", null, null);
        Objects.requireNonNull(getCommand("gc")).setExecutor((CommandExecutor) GeoCommand.baseCommand);

        GeoCommand.adminPermissionGroup = "gc.group.admin";

        // initialise data collections
        DataCollectionManager.init();

        // set up bstats
        bStatsSetup();

        // set up integrations
        IntegrationManager.init();

        // done
        ChatUtil.sendPrefixedLogMessage("Plugin enabled!");
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
