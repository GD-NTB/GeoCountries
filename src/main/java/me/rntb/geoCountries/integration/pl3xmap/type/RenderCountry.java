package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;

import java.util.*;
import java.util.stream.Collectors;

public class RenderCountry {

    private final UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private final Map<String, List<RenderClaimChunk>> worldChunks;
    public Map<String, List<RenderClaimChunk>> getWorldChunks() {
        return Collections.unmodifiableMap(worldChunks);
    }
    public boolean hasAnyWorldChunks() {
        return !worldChunks.isEmpty();
    }

    private final Country country;
    public Country getCountry() {
        return country;
    }

    public static RenderCountry from(Country country) {
        return new RenderCountry(country.getUUID(), country);
    }

    public RenderCountry(UUID uuid, Country country) {
        this.uuid = uuid;
        this.country = country;
        this.worldChunks = buildWorldChunksHashMap(ClaimChunk.get(country));
    }

    private static Map<String, List<RenderClaimChunk>> buildWorldChunksHashMap(List<ClaimChunk> claimChunks) {
        return claimChunks.stream()
                          .collect(Collectors.groupingBy(cc -> cc.getPl3xMapWorld().getName(),
                                   Collectors.mapping(RenderClaimChunk::from, Collectors.toList())));
    }
}
