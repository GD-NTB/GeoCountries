package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.Country;
import net.pl3x.map.core.markers.marker.Polygon;

import java.util.*;

public class CountryRenderData {

    public static final Map<Country, CountryRenderData> map = new HashMap<>();
    public static CountryRenderData get(Country country) {
        if (country == null)
            return null;
        return map.get(country);
    }

    private final Set<Long> chunks = new HashSet<>();
    public Set<Long> getChunks() {
        return chunks;
    }

    private final Set<Edge> edges = new HashSet<>();
    public Set<Edge> getEdges() {
        return edges;
    }

    private final List<Polygon> markers = new ArrayList<>();
    public List<Polygon> getMarkers() {
        return markers;
    }
}