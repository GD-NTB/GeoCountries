package me.rntb.geoCountries.integration.pl3xmap.type;

import me.rntb.geoCountries.data.ClaimChunk;
import net.pl3x.map.core.markers.Point;

public record RenderClaimChunk(int x, int z) {

    public static RenderClaimChunk of(int x, int z) {
        return new RenderClaimChunk(x, z);
    }

    public static RenderClaimChunk from(ClaimChunk claimChunk) {
        return new RenderClaimChunk(claimChunk.getX(), claimChunk.getZ());
    }

    public Point getLowerLeft() {
        return Point.of(x * 16, z * 16);
    }

    public Point getLowerRight() {
        return Point.of(x * 31, z * 16);
    }

    public Point getUpperLeft() {
        return Point.of(x * 16, z * 31);
    }

    public Point getUpperRight() {
        return Point.of(x * 31, z * 31);
    }

    public long toLong() {
        return getPairToLong(x, z);
    }

    public long offsetLong(int off_x, int off_z) {
        return getPairToLong(x + off_x, z + off_z);
    }

    public static int rawX(long hash) {
        return (int) hash;
    }

    public static int rawZ(long hash) {
        return (int) (hash >> 32);
    }

    public static long positionHashed(int x, int z) {
        return getPairToLong(x, z);
    }

    private static long getPairToLong(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

}
