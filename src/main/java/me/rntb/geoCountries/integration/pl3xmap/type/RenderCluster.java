package me.rntb.geoCountries.integration.pl3xmap.type;

import java.util.*;

public class RenderCluster {

    private final Map<Long, RenderClaimChunk> claimChunks = new HashMap<>();

    public RenderCluster() { }

    public boolean isEmpty() {
        return claimChunks.isEmpty();
    }

    public int size() {
        return claimChunks.size();
    }

    public boolean has(RenderClaimChunk rClaimChunk) {
        return claimChunks.containsKey(rClaimChunk.toLong());
    }
    public boolean has(long hash) {
        return claimChunks.containsKey(hash);
    }

    public RenderClaimChunk at(long hash) {
        return claimChunks.get(hash);
    }

    public void add(RenderClaimChunk rClaimChunk) {
        claimChunks.put(rClaimChunk.toLong(), rClaimChunk);
    }
    public void add(long hash, RenderClaimChunk rClaimChunk) {
        claimChunks.put(hash, rClaimChunk);
    }

    public Collection<RenderClaimChunk> getClaimChunks() {
        return Collections.unmodifiableCollection(claimChunks.values());
    }

    private static final int[][] NEIGHBOURS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    public static List<RenderCluster> find(List<RenderClaimChunk> rClaimChunks) {
        if (rClaimChunks == null || rClaimChunks.isEmpty())
            return Collections.emptyList();

        Map<Long, RenderClaimChunk> map = collectionToMap(rClaimChunks);
        List<RenderCluster> clusters = new ArrayList<>();

        while (!map.isEmpty()) {
            RenderCluster cluster = new RenderCluster();
            Deque<Long> stack = new ArrayDeque<>();

            stack.push(map.keySet().iterator().next());

            while (!stack.isEmpty()) {
                long hash = stack.pop();
                RenderClaimChunk chunk = map.remove(hash);

                if (chunk == null)
                    continue;

                cluster.add(hash, chunk);

                for (int[] n : NEIGHBOURS) {
                    long neighbor = chunk.offsetLong(n[0], n[1]);
                    if (map.containsKey(neighbor))
                        stack.push(neighbor);
                }
            }

            if (!cluster.isEmpty())
                clusters.add(cluster);
        }

        return clusters;
    }

    private static Map<Long, RenderClaimChunk> collectionToMap(Collection<RenderClaimChunk> chunks) {
        Map<Long, RenderClaimChunk> map = new HashMap<>((chunks.size() * 4) / 3);
        for (RenderClaimChunk chunk : chunks)
            map.put(chunk.toLong(), chunk);
        return map;
    }

    // O(bounding box area)
    public List<RenderClaimChunk> findNegativeSpace() {
        // can't be any negative space for clusters less than 8
        if (size() < 8)
            return Collections.emptyList();

        RenderEdges edges = getClaimChunks().stream()
                                       .collect(RenderEdges.collect());

        // expand bounding box by 1
        int floodMinX = edges.getMinX() - 1;
        int floodMinZ = edges.getMinZ() - 1;
        int floodMaxX = edges.getMaxX() + 1;
        int floodMaxZ = edges.getMaxZ() + 1;

        // convert everything to boolean grid (faster access)
        int width = floodMaxX - floodMinX + 1;
        int height = floodMaxZ - floodMinZ + 1;

        boolean[][] visited = new boolean[width][height];
        boolean[][] isInCluster = new boolean[width][height];
        for (RenderClaimChunk tb : getClaimChunks()) {
            isInCluster[tb.x() - floodMinX][tb.z() - floodMinZ] = true;
        }

        // start flood fill from outside corner
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[] { 0, 0 });

        while (!stack.isEmpty()) {
            int[] position = stack.pop();
            int x = position[0];
            int z = position[1];

            // if out of bounds, already visited, or is in cluster, skip
            if (x < 0 || z < 0 || x >= width || z >= height || visited[x][z] || isInCluster[x][z])
                continue;

            visited[x][z] = true;

            // add neighbours
            for (int[] n : NEIGHBOURS)
                stack.push(new int[]{x + n[0], z + n[1]});
        }

        // collect holes (chunks that weren't visited by flood fill from outside)
        List<RenderClaimChunk> holes = new ArrayList<>();

        for (int z = edges.getMinZ(); z <= edges.getMaxZ(); z++) {
            for (int x = edges.getMinX(); x <= edges.getMaxX(); x++) {
                long hash = RenderClaimChunk.positionHashed(x, z);
                if (has(hash))
                    continue;

                int gx = x - floodMinX;
                int gz = z - floodMinZ;

                if (!visited[gx][gz])
                    holes.add(RenderClaimChunk.of(x, z));
            }
        }

        return holes;
    }
}
