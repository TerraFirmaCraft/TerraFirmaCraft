/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.river.RiverFractal;

public enum AddRiversAndLakes implements RegionTask
{
    INSTANCE;

    public static final float RIVER_LENGTH = 2.7f;
    public static final int RIVER_DEPTH = 17;
    public static final float RIVER_FEATHER = 0.8f;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final RandomSource random = context.random;

        final RegionRiverGenerator riverGenerator = new RegionRiverGenerator(region);

        createInitialDrains(context, region, riverGenerator);

        final List<RiverEdge> rivers = riverGenerator.buildEdges(e -> new RiverEdge(e, random));

        context.region.setRivers(rivers);
        annotateRiver(region, random, rivers);
    }

    private void createInitialDrains(RegionGenerator.Context context, Region region, RegionRiverGenerator riverGenerator)
    {
        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && point.shore() && context.random.nextInt(3) == 0)
                {
                    // Mark as a possible river source
                    float bestAngle = findBestStartingAngle(region, context.random, index);
                    if (!Float.isNaN(bestAngle))
                    {
                        final XoroshiroRandomSource rng = new XoroshiroRandomSource(context.random.nextLong());
                        riverGenerator.add(new RiverFractal.Builder(rng, region.minX() + dx + 0.5f, region.minZ() + dz + 0.5f, bestAngle, RIVER_LENGTH, RIVER_DEPTH, RIVER_FEATHER));
                        point.setRiver();
                    }
                }
            }
        }
    }

    private float findBestStartingAngle(Region region, RandomSource random, int index)
    {
        // Iterate to find the most likely (projected) river direction to start out
        int bestDistanceMetric = Integer.MIN_VALUE;
        int bestDistanceCount = 0;
        float bestAngle = Float.NaN;

        for (int dirX = -1; dirX <= 1; dirX++)
        {
            for (int dirZ = -1; dirZ <= 1; dirZ++)
            {
                if (dirX == 0 && dirZ == 0) continue;

                final int dirIndex = region.offset(index, 2 * dirX, 2 * dirZ);
                if (dirIndex != -1)
                {
                    final Region.Point dirPoint = region.data()[dirIndex];
                    if (dirPoint != null && dirPoint.land())
                    {
                        final int dirDistanceMetric = dirPoint.distanceToOcean - Math.abs(dirX) - Math.abs(dirZ);
                        if (dirDistanceMetric > bestDistanceMetric || (random.nextInt(1 + bestDistanceCount) == 0))
                        {
                            if (dirDistanceMetric > bestDistanceMetric)
                            {
                                bestDistanceMetric = dirDistanceMetric;
                                bestDistanceCount = 0;
                            }
                            bestDistanceCount += 1;
                            bestAngle = (float) Math.atan2(dirZ, dirX);
                        }
                    }
                }

            }
        }

        return bestAngle;
    }

    private void annotateRiver(Region region, RandomSource random, List<RiverEdge> rivers)
    {
        // 1. Build a multimap of vertex -> edge(s) for each river
        // 2. Use to build a multi-tree of the river network
        final Map<RiverFractal.Vertex, RiverEdge> sourceVertexToEdge = new HashMap<>();
        for (RiverEdge edge : rivers)
        {
            edge.setSource(true); // Initially assign each edge as a source, then remove those that we encounter having an upstream edge
            sourceVertexToEdge.put(edge.source(), edge);
        }

        RiverEdge drain = null;
        for (RiverEdge edge : rivers)
        {
            final RiverEdge downstreamEdge = sourceVertexToEdge.get(edge.drain());

            edge.setDrainEdge(downstreamEdge);
            if (downstreamEdge == null)
            {
                drain = edge;
            }
            else
            {
                downstreamEdge.setSource(false);
            }
        }

        assert drain != null : "River was unable to locate a global drain edge";

        // 3. Based on the source edges, mark sources of rivers as lakes, where we can
        final List<RiverEdge> sourceEdges = new ArrayList<>();
        for (RiverEdge edge : rivers)
        {
            if (edge.isSource())
            {
                final int gridX = Math.round(edge.source().x());
                final int gridZ = Math.round(edge.source().y());

                final Region.Point point = region.maybeAt(gridX, gridZ);
                if (point != null && point.distanceToOcean >= 2 && point.land() && random.nextInt(3) == 0)
                {
                    point.setLake();
                }

                sourceEdges.add(edge);
            }
        }

        // 4. Iterate downstream and annotate width accumulation on each edge
        for (RiverEdge edge : sourceEdges)
        {
            int width = 8;
            while (edge != null)
            {
                edge.width = Math.max(edge.width, width);
                edge = sourceVertexToEdge.get(edge.drain());
                width += 2;
                if (width > 18)
                {
                    width = 18;
                }
            }
        }

        // 5. Apply downstream edge width
        for (RiverEdge edge : rivers)
        {
            final RiverEdge downstreamEdge = sourceVertexToEdge.get(edge.drain());
            if (downstreamEdge != null)
            {
                edge.downstreamWidth = downstreamEdge.width;
            }
        }
    }

    static class RegionRiverGenerator extends RiverFractal.MultiParallelBuilder
    {
        private final Region region;

        RegionRiverGenerator(Region region)
        {
            this.region = region;
        }

        @Override
        protected boolean isLegal(RiverFractal.Vertex prev, RiverFractal.Vertex vertex)
        {
            final Region.Point prevPoint = vertex2Point(prev), newPoint = vertex2Point(vertex);
            return newPoint != null && prevPoint != null && newPoint.land() && newPoint.distanceToOcean >= prevPoint.distanceToOcean;
        }

        @Nullable
        private Region.Point vertex2Point(RiverFractal.Vertex vertex)
        {
            final int gridX = Math.round(vertex.x());
            final int gridZ = Math.round(vertex.y());
            return region.maybeAt(gridX, gridZ);
        }
    }
}
