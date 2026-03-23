package me.rntb.geoCountries.integration.pl3xmap.type;

import net.pl3x.map.core.markers.Point;

public record Edge(Point a, Point b) {

    public Edge {
        // normalize ordering of points
        if (compare(a,b) > 0) {
            Point temp = a;
            a = b;
            b = temp;
        }
    }

    private static int compare(Point p1, Point p2) {
        int cmp = Double.compare(p1.x(), p2.x());
        if (cmp != 0)
            return cmp;
        return Double.compare(p1.z(), p2.z());
    }
}