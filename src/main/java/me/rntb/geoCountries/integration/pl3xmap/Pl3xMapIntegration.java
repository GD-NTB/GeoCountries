package me.rntb.geoCountries.integration.pl3xmap;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.ClaimChunk;
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
import net.pl3x.map.core.markers.marker.Polygon;
import net.pl3x.map.core.markers.marker.Polyline;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // todo: should prob display a total chunk count and a chunk count of the selection, so we should pass in a rendercluster or smthn
    private static Options buildMarkerSettings(Country country) {
        int colour = Colors.fromHex(country.getSettings().get("mapcolour"));
        String motto = country.getSettings().get("motto");
        PlayerProfile leader = country.getLeaderObject();
        Faction faction = country.getFactionObject();
        int size = ClaimChunk.get(country).size();
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

    public static void renderCountry(Country country) {
        RenderCountry rCountry = RenderCountry.from(country);
        if (!rCountry.hasAnyWorldChunks())
            return;

        int id = 0;
        for (Map.Entry<String, List<RenderClaimChunk>> entry : rCountry.getWorldChunks().entrySet()) {
            // get layer
            String worldName = entry.getKey();
            World world = api.getWorldRegistry().get(worldName);
            SimpleLayer layer = layers.get(world);
            if (layer == null)
                continue;

            List<RenderClaimChunk> chunks = entry.getValue();

            // find chunk clusters
            List<RenderCluster> clusters = RenderCluster.find(chunks);

            // build each polygon for each cluster
            for (RenderCluster cluster : clusters) {
                List<PolygonPart> parts = buildPolygonParts(cluster);
                for (PolygonPart part : parts) {
                    List<Polyline> lines = new ArrayList<>();

                    // add outer
                    lines.add(Polyline.of("outer_" + id, part.outer()));

                    // add "hollow" holes
                    for (List<Point> hole : part.holes()) {
                        lines.add(Polyline.of("hole_" + id++, hole));
                    }

                    Polygon polygon = Polygon.of("country_" + country.getName() + "_" + id++, lines);
                    polygon.setOptions(buildMarkerSettings(country));

                    layer.addMarker(polygon);
                }
            }
        }
    }

    public static List<PolygonPart> buildPolygonParts(RenderCluster cluster) {
        Map<Point, List<Point>> graph = RenderEdge.buildEdgeGraphFromCluster(cluster);
        List<List<Point>> loops = RenderEdge.extractPolygonsFromEdgeGraph(graph);

        List<List<Point>> outers = new ArrayList<>();
        List<List<Point>> holes = new ArrayList<>();
        for (List<Point> loop : loops) {
            loop = mergeCollinearPoints(loop);
            if (polygonArea(loop) > 0)
                outers.add(loop);
            else
                holes.add(loop);
        }

        List<PolygonPart> parts = new ArrayList<>();
        for (List<Point> outer : outers) {
            List<List<Point>> inner = new ArrayList<>();

            for (List<Point> hole : holes) {
                if (pointInside(outer, hole.getFirst()))
                    inner.add(hole);
            }

            parts.add(new PolygonPart(outer, inner));
        }

        return parts;
    }

    public static double polygonArea(List<Point> poly) {
        double sum = 0;
        for (int i = 0; i < poly.size(); i++) {
            Point a = poly.get(i);
            Point b = poly.get((i + 1) % poly.size());

            sum += (a.x() * b.z()) - (b.x() * a.z());
        }
        return sum * 0.5;
    }

    public static boolean pointInside(List<Point> poly, Point p) {
        boolean inside = false;

        for (int i = 0, j = poly.size() - 1; i < poly.size(); j = i++) {
            Point a = poly.get(i);
            Point b = poly.get(j);
            // blyat
            if ((a.z() > p.z()) != (b.z() > p.z()) && (p.x() < (b.x() - a.x()) * (p.z() - a.z()) / (b.z() - a.z()) + a.x()))
                inside = !inside;
        }

        return inside;
}

    public static List<Point> mergeCollinearPoints(List<Point> points) {
        int size = points.size();
        if (size < 3)
            return points;

        List<Point> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Point prev = points.get((i - 1 + size) % size);
            Point curr = points.get(i);
            Point next = points.get((i + 1) % size);

            boolean vertical = prev.x() == curr.x() && curr.x() == next.x();
            boolean horizontal = prev.z() == curr.z() && curr.z() == next.z();
            if (!vertical && !horizontal)
                result.add(curr);
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
