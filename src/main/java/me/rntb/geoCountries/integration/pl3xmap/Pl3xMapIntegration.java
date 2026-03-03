package me.rntb.geoCountries.integration.pl3xmap;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.util.ChatUtil;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.Rectangle;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Pl3xMapIntegration {

    public static Pl3xMap api;

    private static final Map<World, SimpleLayer> layers = new HashMap<>();

    public static void init() {
        api = Pl3xMap.api();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Pl3xMapIntegration started!");

        long startTime = System.nanoTime();
        clearAllClaims();

        createLayers();
        addAllClaims();
        long endTime = System.nanoTime();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Finished Pl3xMapIntegration: " + (endTime - startTime) * 0.000001 + "ms");
    }

    private static void createLayers() {
        Registry<@NotNull World> worlds = api.getWorldRegistry();

        for (World world : worlds.values()) {
            SimpleLayer layer = new SimpleLayer("geocountries_claims_" + world.getName(), () -> "GeoCountries Regions");
            layer.setPriority(1);
            layer.setZIndex(1);
            layer.setLiveUpdate(true);

            world.getLayerRegistry().register(layer);

            layers.put(world, layer);
        }
    }

    private static void addAllClaims() {
        for (ClaimChunk claimChunk : ClaimChunk.all) {
            addClaim(claimChunk);
        }
    }

    private static void addClaim(ClaimChunk claimChunk) {
        SimpleLayer layer = layers.get(claimChunk.getPl3xMapWorld());

        // build claim marker
        String key = "CLAIMCHUNK_FILL(" + claimChunk.getX() + ", " + claimChunk.getZ() + ")";
        Point corner1 = Point.of(claimChunk.getX() * 16, claimChunk.getZ() * 16);
        Point corner2 = Point.of((claimChunk.getX()+1) * 16, (claimChunk.getZ()+1) * 16);
        Rectangle chunkMarker = new Rectangle(key, corner1, corner2);

        int white = Colors.setAlpha(128, Colors.rgb(255, 255, 255));
        int black = Colors.rgb(0, 0, 0);
        chunkMarker.setOptions(Options.builder()
                                      .tooltipContent("tool tip content")
                                      .fillColor(white)
                                      .strokeColor(black)
                                      .strokeWeight(255)
                                      .fill(true)
                                      .stroke(true)
                                      .build()
        );


        layer.addMarker(chunkMarker);
    }

    private static void clearAllClaims() {
        for (SimpleLayer layer : layers.values()) {
            if (layer == null)
                continue;
            layer.getMarkers().clear();
        }
    }
}
