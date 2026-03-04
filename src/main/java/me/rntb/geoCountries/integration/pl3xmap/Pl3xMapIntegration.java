package me.rntb.geoCountries.integration.pl3xmap;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.util.ChatUtil;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.marker.Rectangle;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class  Pl3xMapIntegration {

    // todo: queue

    public static Pl3xMap api;

    private static final Map<World, SimpleLayer> layersByPl3xMapWorld = new HashMap<>();

    public static void init() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

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

            layersByPl3xMapWorld.put(world, layer);
        }
    }

    public static void addAllClaims() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        for (ClaimChunk claimChunk : ClaimChunk.all) {
            addClaim(claimChunk);
        }
    }

    private static Options buildMarkerSettings(String tooltip, String colour, int fillAlpha) {
        int colourHex = Colors.fromHex(colour);
        return Options.builder()
                      .tooltipContent(tooltip)
                      .stroke(true)
                      .strokeColor(colourHex)
                      .fill(true)
                      .fillColor(Colors.setAlpha(fillAlpha, colourHex))
                      .build();
    }

    public static void addClaim(ClaimChunk claimChunk) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        Country owner = claimChunk.getOwnerCountry();

        SimpleLayer layer = layersByPl3xMapWorld.get(claimChunk.getPl3xMapWorld());

        // build claim marker
        // key = claimChunk(x,z)
        Point corner1 = Point.of(claimChunk.getX() * 16, claimChunk.getZ() * 16);
        Point corner2 = Point.of((claimChunk.getX()+1) * 16, (claimChunk.getZ()+1) * 16);
        Rectangle chunkMarker = new Rectangle(claimChunk.getPl3xMapKey(), corner1, corner2);

        chunkMarker.setOptions(buildMarkerSettings("insert stuff here",
                                                   owner.settings.get("mapcolour"),
                                                   128));

        layer.addMarker(chunkMarker);
    }

    public static void clearAllClaims() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        for (SimpleLayer layer : layersByPl3xMapWorld.values()) {
            if (layer == null)
                continue;
            layer.getMarkers().clear();
        }
    }

    public static void clearClaim(ClaimChunk claimChunk) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        SimpleLayer layer = layersByPl3xMapWorld.get(claimChunk.getPl3xMapWorld());
        layer.removeMarker(claimChunk.getPl3xMapKey());
    }

    public static void reloadClaimColour(ClaimChunk claimChunk) {
        // get chunk
        SimpleLayer layer = layersByPl3xMapWorld.get(claimChunk.getPl3xMapWorld());
        Marker<?> marker = layer.registeredMarkers().get(claimChunk.getPl3xMapKey());
        if (marker == null)
            return;

        Country owner = claimChunk.getOwnerCountry();

        marker.setOptions(buildMarkerSettings("insert stuff here",
                                              owner.settings.get("mapcolour"),
                                              128));
    }

    // expensive!!
    // todo: queue!!!!!
    public static void reloadClaimsColourOfCountry(Country country) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        for (ClaimChunk claimChunk : country.getClaimChunks()) {
            reloadClaimColour(claimChunk);
        }
    }
}
