package me.rntb.geoCountries;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.rntb.geoCountries.command.GeoCommand;
import me.rntb.geoCountries.command.gc;
import me.rntb.geoCountries.config.ConfigManager;
import me.rntb.geoCountries.data.DataCollectionManager;
import me.rntb.geoCountries.integration.IntegrationManager;
import me.rntb.geoCountries.listener.*;
import me.rntb.geoCountries.service.CitizenshipApplicationService;
import me.rntb.geoCountries.type.Response;
import me.rntb.geoCountries.util.ChatUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Objects;

// todo: banners as flags
// todo: markers
// todo: javadocs eventually
public class GeoCountries extends JavaPlugin {

    public static JavaPlugin self; // plugin instance

    public static Path pluginAbsoluteDataFolderPath;

    // prevents potentially overwriting data with shite if error on startup
    public static boolean willSaveData = false;

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
        pluginManager.registerEvents(new InventoryGUIListener(), this);
        pluginManager.registerEvents(new PlayerChunkEnterListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PrefixChatListener(), this);
        pluginManager.registerEvents(new ResponseChatListener(), this);

        // initialise base command
        GeoCommand.setBaseCommand(new gc("gc", "gc", null));

        // register base command
        Objects.requireNonNull(getCommand(GeoCommand.getBaseCommand().getName())).setExecutor((CommandExecutor) GeoCommand.getBaseCommand());

        GeoCommand.setAdminPermissionGroup("gc.group.admin");

        // initialise data collections
        DataCollectionManager.init();

        // set up bstats
        bStatsSetup();

        // set up integrations
        IntegrationManager.init();

        // add response on failure methods (is this the best way to do this?)
        Response.getOnFailMethods().add(CitizenshipApplicationService::onResponseFail);

        // done
        ChatUtil.sendPrefixedLogMessage("Plugin enabled!");

        willSaveData = true;
    }

    @Override
    public void onDisable() {
        // save data collections
        if (willSaveData)
            DataCollectionManager.save();

        // do integration shutdown stuff
        IntegrationManager.disable();

        ChatUtil.sendPrefixedLogMessage("Plugin disabled!");
    }

    private void bStatsSetup() {
        int pluginId = 29384;
        new Metrics(this, pluginId);
    }
}
