package me.rntb.geoCountries.integration.pl3xmap;

import me.rntb.geoCountries.config.ConfigState;
import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;
import me.rntb.geoCountries.data.Faction;
import me.rntb.geoCountries.data.PlayerProfile;
import me.rntb.geoCountries.integration.IntegrationState;
import me.rntb.geoCountries.integration.pl3xmap.type.CountryRenderData;
import me.rntb.geoCountries.integration.pl3xmap.type.Edge;
import me.rntb.geoCountries.integration.pl3xmap.type.PolygonPart;
import me.rntb.geoCountries.util.ChatUtil;
import me.rntb.geoCountries.util.StringUtil;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.Polygon;
import net.pl3x.map.core.markers.marker.Polyline;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;

import java.util.*;

public class Pl3xMapIntegration {

    public static Pl3xMap api;

    private static World claimWorld;
    private static SimpleLayer claimLayer;

    public static void init() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        api = Pl3xMap.api();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Pl3xMapIntegration started!");

        // look at my fancy splancy timer
        /* TIMER START */ long startTime = System.nanoTime();

        if (!createClaimLayer()) {
            IntegrationState.isPl3xMapEnabled = false;
            ConfigState.enablePl3xMap = false; // might as well
            return;
        }
        clearAndDrawAll();

        /* TIMER END */ long endTime = System.nanoTime();

        if (ConfigState.debugLogging)
            ChatUtil.sendPrefixedLogMessage("Finished Pl3xMapIntegration: " + (endTime - startTime) * 0.000001 + "ms");
    }

    // --- init ---
    private static void clearAndDrawAll() {
        clearAll();

        for (Country country : Country.getAll()) {
            initCountry(country);
            drawCountry(country);
        }
    }

    public static void clearAll() {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        claimLayer.getMarkers().clear();
        CountryRenderData.map.clear();
    }

    private static void initCountry(Country country) {
        CountryRenderData crData = new CountryRenderData();

        // load chunks
        for (ClaimChunk cc : ClaimChunk.get(country)) {
            addChunk(crData,  cc.getX(), cc.getZ());
        }

        CountryRenderData.map.put(country, crData);
    }

    private static boolean createClaimLayer() {
        claimWorld = api.getWorldRegistry().get(ConfigState.claimWorld);
        if (claimWorld == null) {
            ChatUtil.sendPrefixedLogMessage("Couldn't find world by name of '" + ConfigState.claimWorld + "', aborting! (check the config)!");
            return false;
        }

        claimLayer = new SimpleLayer("geocountries_claims_" + claimWorld.getName(), () -> "GeoCountries Claims");
        claimLayer.setPriority(1);
        claimLayer.setZIndex(1);
        claimLayer.setLiveUpdate(true);

        claimWorld.getLayerRegistry().register(claimLayer);

        return true;
    }

    // --- style ---
    private static Options buildMarkerSettings(Country country) {
        int colour = Colors.fromHex(country.getSettings().get("mapcolour"));

        String motto = country.getSettings().get("motto");

        Faction faction = country.getFactionObject();
        PlayerProfile leader = country.getLeaderObject();

        int size = country.getClaimChunksCount();
        float sizePercent;
        if (size == 0 || ClaimChunk.getAll().isEmpty())
            sizePercent = 0;
        else
            sizePercent = ((float) size / ClaimChunk.getAll().size())*100;

        return Options.builder()
                      .tooltipContent("""
                                      <span style="font-size:20px"><b>%s</b></span><br>
                                      <i>%s</i><br>
                                      <br>
                                      <b>Faction</b>: %s<br>
                                      <b>Leader</b>: %s<br>
                                      <b>Citizens</b>: %d<br>
                                      <b>Size</b>: %d chunk%s (%.1f%%)"""
                                      .formatted(country.getName(),
                                                 motto.equals("null") ? "No motto" : motto,
                                                 faction == null ? "None" : faction.getName(),
                                                 leader == null ? "None" : leader.getUsername(),
                                                 country.getCitizenCount(),
                                                 size, StringUtil.leadingS(size), sizePercent))
                      .stroke(true)
                      .strokeColor(Colors.setAlpha(255, colour))
                      .strokeWeight(4)
                      .fill(true)
                      .fillColor(Colors.setAlpha(128, colour))
                      .build();
    }

    // --- build and draw ---
    private static void drawCountry(Country country) {
        CountryRenderData crData = CountryRenderData.get(country);
        if (crData == null)
            return;

        for (Polygon poly : crData.getMarkers()) {
            claimLayer.removeMarker(poly.getKey());
        }
        crData.getMarkers().clear();

        Map<Point,List<Point>> graph = buildGraph(crData.getEdges());
        List<List<Point>> loops = extractLoops(graph);
        List<PolygonPart> parts = buildParts(loops);

        crData.getMarkers().clear();

        int i = 0;
        for (PolygonPart part : parts) {
            List<Polyline> lines = new ArrayList<>();

            // add outer
            lines.add(Polyline.of("outer_" + i, part.outer()));

            // add holes
            for (List<Point> hole : part.holes()) {
                lines.add(Polyline.of("hole_" + i, hole));
                i++;
            }

            // build into polygon
            String key = "country_" + country.getName() + "_" + i++;
            Polygon polygon = Polygon.of(key, lines);
            polygon.setOptions(buildMarkerSettings(country));

            claimLayer.addMarker(polygon);

            crData.getMarkers().add(polygon);
        }
    }

    private static Map<Point,List<Point>> buildGraph(Set<Edge> edges) {
        Map<Point,List<Point>> graph = new HashMap<>();

        for (Edge edge : edges) {
            graph.computeIfAbsent(edge.a(), k -> new ArrayList<>()).add(edge.b());
            graph.computeIfAbsent(edge.b(), k -> new ArrayList<>()).add(edge.a());
        }

        return graph;
    }

    private static List<List<Point>> extractLoops(Map<Point, List<Point>> graph) {
        Set<String> visitedEdges = new HashSet<>();
        List<List<Point>> loops = new ArrayList<>();

        for (Map.Entry<Point, List<Point>> entry : graph.entrySet()) {
            Point start = entry.getKey();

            for (Point next : entry.getValue()) {
                String edgeKey = edgeKey(start, next);
                if (visitedEdges.contains(edgeKey))
                    continue;

                Point prev = start;
                Point current = next;

                List<Point> loop = new ArrayList<>();
                loop.add(start);

                while (true) {
                    loop.add(current);
                    visitedEdges.add(edgeKey(prev, current));

                    List<Point> neighbors = graph.get(current);
                    Point nextPoint = null;

                    for (Point candidate : neighbors) {
                        String key = edgeKey(current, candidate);
                        if (!candidate.equals(prev) && !visitedEdges.contains(key)) {
                            nextPoint = candidate;
                            break;
                        }
                    }
                    if (nextPoint == null)
                        break;

                    prev = current;
                    current = nextPoint;

                    if (current.equals(start))
                        break;
                }

                if (loop.size() >= 3)
                    loops.add(loop);
            }
        }

        return loops;
    }

    private static List<PolygonPart> buildParts(List<List<Point>> loops) {
        List<List<Point>> outers = new ArrayList<>();
        List<List<Point>> holes = new ArrayList<>();

        for (List<Point> loop : loops) {
            boolean isHole = false;
            for (List<Point> other : loops) {
                if (other == loop) continue;

                if (pointInside(other, loop.getFirst())) {
                    isHole = true;
                    break;
                }
            }
            if (isHole)
                holes.add(loop);
            else
                outers.add(loop);
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

    // --- helpers ---
    private static String edgeKey(Point a, Point b) {
        if (compare(a, b) > 0) {
            Point tmp = a;
            a = b;
            b = tmp;
        }
        return a.x() + "," + a.z() + "|" + b.x() + "," + b.z();
    }

    private static int compare(Point p1, Point p2) {
        int cmp = Double.compare(p1.x(), p2.x());
        if (cmp != 0) return cmp;
        return Double.compare(p1.z(), p2.z());
    }

    private static boolean pointInside(List<Point> polygon, Point p) {
        boolean inside = false;

        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            Point a = polygon.get(i);
            Point b = polygon.get(j);
            // blyat
            if ((a.z() > p.z()) != (b.z() > p.z()) && (p.x() < (b.x() - a.x()) * (p.z() - a.z()) / (b.z() - a.z()) + a.x()))
                inside = !inside;
        }

        return inside;
    }

    // --- controls ---
    private static void addChunk(CountryRenderData crData, int x, int z) {
        crData.getChunks().add(ClaimChunk.packToKey(x, z));
        toggleNeighbours(crData, x, z);
    }

    private static void removeChunk(CountryRenderData crData, int x, int z) {
        crData.getChunks().remove(ClaimChunk.packToKey(x, z));
        toggleNeighbours(crData, x, z);
    }

    private static void toggleNeighbours(CountryRenderData crData, int x, int z) {
        checkEdge(crData, p(x+1, z),   p(x+1, z+1));
        checkEdge(crData, p(x, z+1),   p(x, z));
        checkEdge(crData, p(x+1, z+1), p(x, z+1));
        checkEdge(crData, p(x, z),     p(x+1, z));
    }
    private static void checkEdge(CountryRenderData crData, Point a, Point b) {
        Edge edge = new Edge(a, b);
        if (!crData.getEdges().add(edge))
            crData.getEdges().remove(edge);
    }
    private static Point p(int x, int z) {
        return Point.of(x * 16, z * 16);
    }

    public static void clearCountry(Country country) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        CountryRenderData data = CountryRenderData.get(country);
        if (data == null)
            return;

        for (Polygon poly : data.getMarkers()) {
            claimLayer.removeMarker(poly.getKey());
        }

        data.getMarkers().clear();

        data.getEdges().clear();
        data.getChunks().clear();

        CountryRenderData.map.remove(country);
    }

    public static void updateCountryStyle(Country country) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        CountryRenderData data = CountryRenderData.get(country);
        if (data == null)
            return;

        Options newOptions = buildMarkerSettings(country);
        for (Polygon polygon : data.getMarkers()) {
            polygon.setOptions(newOptions); // update requires page refresh :(
        }
    }

    // --- events ---
    public static void onClaim(Country country, int x, int z) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        CountryRenderData crData = CountryRenderData.map.computeIfAbsent(country, c -> new CountryRenderData());
        addChunk(crData, x, z);

        drawCountry(country);
    }

    public static void onUnclaim(Country country, int x, int z) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        CountryRenderData crData = CountryRenderData.map.get(country);
        if (crData == null)
            return;

        removeChunk(crData, x, z);

        drawCountry(country);
    }

    public static void onStyleUpdate(Country country) {
        if (!IntegrationState.isPl3xMapEnabled)
            return;

        updateCountryStyle(country);
    }
}
