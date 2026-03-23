package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.Country;

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

    private List<String> markerIDs = new ArrayList<>();
    public List<String> getMarkerIDs() {
        return markerIDs;
    }
}