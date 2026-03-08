package me.rntb.geoCountries.integration.pl3xmap.type;

import java.util.stream.Collector;

public class RenderEdges {

    private int minX = Integer.MAX_VALUE;
    public int getMinX() {
        return minX;
    }

    private int minZ = Integer.MAX_VALUE;
    public int getMinZ() {
        return minZ;
    }

    private int maxX = Integer.MIN_VALUE;
    public int getMaxX() {
        return maxX;
    }

    private int maxZ = Integer.MIN_VALUE;
    public int getMaxZ() {
        return maxZ;
    }

    private RenderEdges() { }

    private void accept(RenderClaimChunk rClaimChunk) {
        if (rClaimChunk.x() < minX)
            minX = rClaimChunk.x();
        if (rClaimChunk.z() < minZ)
            minZ = rClaimChunk.z();

        if (rClaimChunk.x() > maxX)
            maxX = rClaimChunk.x();
        if (rClaimChunk.z() > maxZ)
            maxZ = rClaimChunk.z();
    }

    private RenderEdges combine(RenderEdges other) {
        if (other.minX < this.minX)
            this.minX = other.minX;
        if (other.minZ < this.minZ)
            this.minZ = other.minZ;

        if (other.maxX > this.maxX)
            this.maxX = other.maxX;
        if (other.maxZ > this.maxZ)
            this.maxZ = other.maxZ;

        return this;
    }

    public static Collector<RenderClaimChunk, RenderEdges, RenderEdges> collect() {
        return Collector.of(RenderEdges::new,
                            RenderEdges::accept,
                            RenderEdges::combine,
                            Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED);
    }
}
