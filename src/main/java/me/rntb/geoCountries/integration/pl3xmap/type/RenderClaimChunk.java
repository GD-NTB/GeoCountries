package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.ClaimChunk;

public record RenderClaimChunk(int x, int z) {

    public static RenderClaimChunk of(int x, int z) {
        return new RenderClaimChunk(x, z);
    }

    public static RenderClaimChunk from(ClaimChunk claimChunk) {
        return new RenderClaimChunk(claimChunk.getX(), claimChunk.getZ());
    }

    public long toLong() {
        return getPairToLong(x, z);
    }

    public long offsetLong(int off_x, int off_z) {
        return getPairToLong(x + off_x, z + off_z);
    }

    public static long positionHashed(int x, int z) {
        return getPairToLong(x, z);
    }

    private static long getPairToLong(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

}
