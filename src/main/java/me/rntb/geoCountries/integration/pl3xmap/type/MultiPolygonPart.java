package me.rntb.geoCountries.integration.pl3xmap.type;

import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.marker.Polygon;
import net.pl3x.map.core.markers.marker.Polyline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record MultiPolygonPart(List<Point> outer, List<List<Point>> holes) {

    public MultiPolygonPart(List<Point> outer, List<List<Point>> holes) {
        this.outer = outer;
        this.holes = holes == null ? Collections.emptyList() : holes;
    }

    public static List<Polygon> toPolygons(List<MultiPolygonPart> parts) {
        List<Polygon> polygons = new ArrayList<>();

        for (MultiPolygonPart part : parts) {
            List<Polyline> lines = new ArrayList<>();

            // add outer polygon
            lines.add(Polyline.of("outer_" + UUID.randomUUID(), part.outer()));

            // add all hole polygons
            for (List<Point> hole : part.holes()) {
                lines.add(Polyline.of("hole_" + UUID.randomUUID(), hole));
            }

            // convert polygon list to pl3xmap polygon
            Polygon polygon = Polygon.of("polygon_" + UUID.randomUUID(), lines);

            polygons.add(polygon);
        }

        return polygons;
    }
}