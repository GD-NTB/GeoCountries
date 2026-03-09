package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.ClaimChunk;
import me.rntb.geoCountries.data.Country;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RenderCountry {

    private final UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final String mapColour;
    public String getMapColour() {
        return mapColour;
    }

    private final String clickTooltip;
    public String getClickTooltip() {
        return clickTooltip;
    }

    private final Map<String, List<RenderClaimChunk>> worldChunks;
    public Map<String, List<RenderClaimChunk>> getWorldChunks() {
        return Collections.unmodifiableMap(worldChunks);
    }
    public boolean hasAnyWorldChunks() {
        return !worldChunks.isEmpty();
    }

    public static RenderCountry from(Country country) {
        return new RenderCountry(country.getUUID(),
                                 country.getName(),
                                 country.getSettings().get("mapcolour"),
                                 "hover text",
                                 "click text",
                                 country.getClaimChunks());
    }

    public RenderCountry(UUID uuid, String name, String mapColour, String hoverText, String clickTooltip, List<Long> claimChunks) {
        this.uuid = uuid;
        this.name = name;
        this.mapColour = mapColour;
        this.clickTooltip = clickTooltip;

        this.worldChunks = buildWorldChunksHashMap(claimChunks.stream()
                                                              .map(ClaimChunk::get).toList());
    }

    private static Map<String, List<RenderClaimChunk>> buildWorldChunksHashMap(List<ClaimChunk> claimChunks) {
        return claimChunks.stream()
                          .collect(Collectors.groupingBy(cc -> cc.getPl3xMapWorld().getName(),
                                   Collectors.mapping(RenderClaimChunk::from, Collectors.toList())));
    }
}
