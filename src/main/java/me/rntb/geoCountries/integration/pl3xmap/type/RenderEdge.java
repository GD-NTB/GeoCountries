package me.rntb.geoCountries.integration.pl3xmap.type;

import net.pl3x.map.core.markers.Point;

import java.util.*;

public record RenderEdge(Point a, Point b) {

    // O(n)
    public static Map<Point, List<Point>> buildEdgeGraphFromCluster(RenderCluster cluster) {
        Map<Point, List<Point>> graph = new HashMap<>();

        for (RenderClaimChunk tb : cluster.getClaimChunks()) {
            int x = tb.x();
            int z = tb.z();

            // right edge
            if (!cluster.has(tb.offsetLong(1, 0)))
                addEdgeToGraph(graph,
                        new Point((x + 1) * 16, z * 16),
                        new Point((x + 1) * 16, (z + 1) * 16));

            // left edge
            if (!cluster.has(tb.offsetLong(-1, 0))) {
                addEdgeToGraph(graph,
                        new Point(x * 16, (z + 1) * 16),
                        new Point(x * 16, z * 16));
            }

            // upper edge
            if (!cluster.has(tb.offsetLong(0, 1))) {
                addEdgeToGraph(graph,
                        new Point((x + 1) * 16, (z + 1) * 16),
                        new Point(x * 16, (z + 1) * 16));
            }

            // lower edge
            if (!cluster.has(tb.offsetLong(0, -1)))
                addEdgeToGraph(graph,
                        new Point(x * 16, z * 16),
                        new Point((x + 1) * 16, z * 16));
        }

        return graph;
    }

    private static void addEdgeToGraph(Map<Point, List<Point>> graph, Point a, Point b) {
        graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
    }

    public static List<List<Point>> extractPolygonsFromEdgeGraph(Map<Point, List<Point>> graph) {
        List<List<Point>> polygons = new ArrayList<>();

        while (!graph.isEmpty()) {
            Point start = graph.keySet().iterator().next();
            Point current = start;

            List<Point> polygon = new ArrayList<>();

            do {
                polygon.add(current);

                List<Point> nextPoints = graph.get(current);
                Point next = nextPoints.removeLast();

                if (nextPoints.isEmpty())
                    graph.remove(current);

                current = next;

            } while (!current.equals(start));

            polygons.add(polygon);
        }

        return polygons;
    }
}
