package me.rntb.geoCountries.integration.pl3xmap;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.type.*;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.MultiPolygon;
import net.pl3x.map.core.markers.marker.Polygon;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Pl3xMapIntegration {

    // todo: queue

    public static Pl3xMap api;

    private static final Map<World, SimpleLayer> layers = new HashMap<>();

    public static void init() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        api = Pl3xMap.api();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Pl3xMapIntegration started!");

        long startTime = System.nanoTime();
        createLayers();
        clearAndDrawAll();
        long endTime = System.nanoTime();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Finished Pl3xMapIntegration: " + (endTime - startTime) * 0.000001 + "ms");
    }

    public static void clearAndDrawAll() {
        clearAllClaims();
        for (Country country : Country.all) {
            renderCountry(country);
        }
    }

    private static void createLayers() {
        layers.clear();

        Registry<@NotNull World> worlds = api.getWorldRegistry();

        for (World world : worlds.values()) {
            SimpleLayer layer = new SimpleLayer("geocountries_claims_" + world.getName(), () -> "GeoCountries Overlay");
            layer.setPriority(1);
            layer.setZIndex(1);
            layer.setLiveUpdate(true);

            world.getLayerRegistry().register(layer);

            layers.put(world, layer);
        }
    }

    private static Options buildMarkerSettings(Country country) {
        int colour = Colors.fromHex(country.getSettings().get("mapcolour"));
        String motto = country.getSettings().get("motto");
        PlayerProfile leader = country.getLeaderPlayerProfile();
        Faction faction = country.getFactionFaction();
        int size = country.getClaimChunksCount();
        return Options.builder()
                      .tooltipContent("""
                                      <span style="font-size:20px"><b>%s</b></span><br>
                                      <i>%s</i><br>
                                      <br>
                                      <b>Faction</b>: %s<br>
                                      <b>Leader</b>: %s<br>
                                      <b>Citizens</b>: %d<br>
                                      <b>Size</b>: %d chunk%s"""
                                      .formatted(country.getName(),
                                                 motto.equals("null") ? "No motto" : motto,
                                                 faction == null ? "None" : faction.getName(),
                                                 leader == null ? "None" : leader.getUsername(),
                                                 country.getCitizenCount(),
                                                 size, StringUtil.leadingS(size)))
                      .stroke(true)
                      .strokeColor(Colors.setAlpha(255, colour))
                      .strokeWeight(4)
                      .fill(true)
                      .fillColor(Colors.setAlpha(128, colour))
                      .build();
    }

    private static void renderCountry(Country country) {
        RenderCountry rCountry = RenderCountry.from(country);

        if (!rCountry.hasAnyWorldChunks())
            return;

        for (Map.Entry<String, List<RenderClaimChunk>> worldChunkEntry : rCountry.getWorldChunks().entrySet()) {
            // get layer
            String worldName = worldChunkEntry.getKey();
            World world = api.getWorldRegistry().get(worldName);
            SimpleLayer layer = layers.get(world);
            if (layer == null)
                continue;

            // get chunks
            List<RenderClaimChunk> rClaimChunks = worldChunkEntry.getValue();

            // convert chunks to clusters
            List<RenderCluster> clusters = RenderCluster.find(rClaimChunks);

            List<MultiPolygonPart> polygonParts = new ArrayList<>();

            // convert clusters to polygons
            for (RenderCluster cluster : clusters) {
                // calculate negative space
                List<RenderClaimChunk> holeChunks = cluster.findNegativeSpace();

                // convert holes to polygons
                List<List<Point>> holesPolygons = new ArrayList<>();
                if (!holeChunks.isEmpty()) {
                    List<RenderCluster> holeClusters = RenderCluster.find(holeChunks);

                    holesPolygons = holeClusters.stream()
                                                .map(Pl3xMapIntegration::buildPolygonFromCluster)
                                                .filter(Objects::nonNull)
                                                .map(Pl3xMapIntegration::mergeCollinearPoints).toList();
                }

                List<Point> mainPolygon = buildPolygonFromCluster(cluster);
                if (mainPolygon == null) {
                    ChatUtil.sendPrefixedLogErrorMessage("Pl3xMapIntegration: Error drawing polygon of country " + country.getName());
                    continue;
                }

                int before = mainPolygon.size();
                mainPolygon = mergeCollinearPoints(mainPolygon);
                int after = mainPolygon.size();

                if (ConfigState.debugLogging)
                    ChatUtil.sendPrefixedLogMessage("Pl3xMapIntegration: Polygon simplified: " + before + " -> " + after);

                polygonParts.add(new MultiPolygonPart(mainPolygon, holesPolygons));
            }

            // if there is no polygon, don't try to draw anything
            if (polygonParts.isEmpty())
                continue;

            // assemble polygons into final multipolygons
            List<Polygon> polygons = MultiPolygonPart.toPolygons(polygonParts);
            MultiPolygon multiPolygon = MultiPolygon.of("MultiPolygon(" + country.getName() + ")", polygons);

            multiPolygon.setOptions(buildMarkerSettings(country));

            // FINALLY draw the multipolygons
            layer.addMarker(multiPolygon);
        }
    }

    private static List<Point> buildPolygonFromCluster(RenderCluster cluster) {
        Map<Point,List<Point>> graph = RenderEdge.buildEdgeGraphFromCluster(cluster);

        List<List<Point>> polygons = RenderEdge.extractPolygonsFromEdgeGraph(graph);

        // largest polygon will be outer boundary
        polygons.sort((a, b) -> Integer.compare(b.size(), a.size()));

        return polygons.isEmpty() ? Collections.emptyList() : polygons.getFirst();
    }

    public static List<Point> mergeCollinearPoints(List<Point> points) {
        int size = points.size();

        // can't merge if less than 3 points
        if (size < 3)
            return points;

        List<Point> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Point previous = points.get((i - 1 + size) % size);
            Point current = points.get(i);
            Point next = points.get((i + 1) % size);

            boolean horizontal = previous.x() == current.x() && current.x() == next.x();
            boolean vertical = previous.z() == current.z() && current.z() == next.z();

            if (!horizontal && !vertical)
                result.add(current);
        }

        return result;
    }

    public static void clearAllClaims() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        for (SimpleLayer layer : layers.values()) {
            if (layer == null)
                continue;
            layer.getMarkers().clear();
        }
    }
}
