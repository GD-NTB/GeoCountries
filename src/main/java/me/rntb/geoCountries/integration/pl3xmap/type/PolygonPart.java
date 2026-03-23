package me.rntb.geoCountries.integration.pl3xmap.type;

import net.pl3x.map.core.markers.Point;

import java.util.List;

public record PolygonPart(List<Point> outer, List<List<Point>> holes) { }