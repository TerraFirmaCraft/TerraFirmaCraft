/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.river.River;

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

        final List<RiverEdge> rivers = riverGenerator.build(e -> new RiverEdge(e, random));

        context.region.setRivers(rivers);
        if (!rivers.isEmpty())
        {
            annotateRiver(region, random, rivers);
        }
    }

    private void createInitialDrains(RegionGenerator.Context context, Region region, RegionRiverGenerator riverGenerator)
    {
        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && point.shore())
                {
                    // Mark as a possible river source
                    float bestAngle = findBestStartingAngle(region, context.random, index);
                    if (!Float.isNaN(bestAngle))
                    {
                        final XoroshiroRandomSource rng = new XoroshiroRandomSource(context.random.nextLong());
                        riverGenerator.add(new River.Builder(rng, region.minX() + dx + 0.5f, region.minZ() + dz + 0.5f, bestAngle, RIVER_LENGTH, RIVER_DEPTH, RIVER_FEATHER));
                        point.setRiver();
                    }
                }
            }
        }
    }

    private float findBestStartingAngle(Region region, RandomSource random, int index)
    {
        // Iterate to find the most likely (projected) river direction to start out
        // Selects the best angle, out of eight choices, and if there are multiple ideal choices, will select uniformly
        // Then, applies a slight variance on the chosen angle, so rivers don't start at exact pi/4 increments, as the river builder will respect the starting angle exactly.
        float bestDistanceMetric = Float.MIN_VALUE;
        int bestDistanceCount = 0;
        float bestAngle = Float.NaN;

        for (int dirX = -1; dirX <= 1; dirX++)
        {
            for (int dirZ = -1; dirZ <= 1; dirZ++)
            {
                if (dirX == 0 && dirZ == 0) continue;

                final int dirIndex = region.offset(index, 4 * dirX, 4 * dirZ);
                if (dirIndex != -1)
                {
                    final Region.Point dirPoint = region.data()[dirIndex];
                    if (dirPoint != null && dirPoint.land())
                    {
                        final float dirDistanceMetric = dirPoint.distanceToOcean - Math.abs(dirX) - Math.abs(dirZ);
                        if (dirDistanceMetric > bestDistanceMetric || (dirDistanceMetric == bestDistanceMetric && random.nextInt(1 + bestDistanceCount) == 0))
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
        if (!Float.isNaN(bestAngle))
        {
            bestAngle += random.nextFloat() * 0.2f - 0.1f; // The rough area covered by each angle is pi/4 ~ 0.75, this gives each angle some wiggle room, but still directs it in the general vicinity of the target angle.
        }

        return bestAngle;
    }

    private void annotateRiver(Region region, RandomSource random, List<RiverEdge> rivers)
    {
        // Build a map of each source vertex to the downstream edge.
        // Use this to populate the source -> drain linked list, so we can traverse down each branch
        final Map<River.Vertex, RiverEdge> sourceVertexToEdge = new HashMap<>();
        for (RiverEdge edge : rivers)
        {
            sourceVertexToEdge.put(edge.source(), edge);
        }

        for (RiverEdge edge : rivers)
        {
            edge.linkToDrain(sourceVertexToEdge.get(edge.drain()));
        }

        // Iterate downstream from each global source edge, and increment width as we go downstream.
        for (RiverEdge edge : rivers)
        {
            if (!edge.sourceEdge())
            {
                int width = RiverEdge.MIN_WIDTH;
                while (edge != null)
                {
                    edge.width = Math.max(edge.width, width);
                    edge = edge.drainEdge();
                    width = Math.min(width + 2, RiverEdge.MAX_WIDTH);
                }
            }
        }

        // Place lakes around the source of rivers.
        for (RiverEdge edge : rivers)
        {
            if (!edge.sourceEdge() && random.nextInt(3) == 0)
            {
                // Try and place a lake near this source
                placeLakeNear(region, edge, 1, 1);
                placeLakeNear(region, edge, -1, 1);
                placeLakeNear(region, edge, 1, -1);
                placeLakeNear(region, edge, -1, -1);
            }
        }
    }

    private void placeLakeNear(Region region, RiverEdge edge, int offsetX, int offsetZ)
    {
        final int gridX = (int) (edge.source().x() + 0.3f * offsetX);
        final int gridZ = (int) (edge.source().y() + 0.3f * offsetZ);

        final Region.Point point = region.maybeAt(gridX, gridZ);
        if (point != null && point.land() && point.distanceToOcean >= 2 && point.distanceToEdge >= 2 && TFCLayers.hasLake(point.biome))
        {
            point.biome = TFCLayers.lakeFor(point.biome);
            point.rainfall += 0.09f * (500f - point.rainfall); // Small, localized rainfall increase around lakes of ~45mm max
        }
    }

    static class RegionRiverGenerator extends River.MultiParallelBuilder
    {
        private final Region region;

        RegionRiverGenerator(Region region)
        {
            this.region = region;
        }

        @Override
        protected boolean isLegal(River.Vertex prev, River.Vertex vertex)
        {
            final Region.Point prevPoint = vertex2Point(prev), newPoint = vertex2Point(vertex);
            return newPoint != null && prevPoint != null
                && newPoint.land() // River must be on land
                && newPoint.distanceToOcean >= prevPoint.distanceToOcean // Further from the ocean or equal than the previous point
                && newPoint.distanceToOcean >= Math.min(3, prev.distance() / 2); // And it should gradually work it's way inland
        }

        @Nullable
        private Region.Point vertex2Point(River.Vertex vertex)
        {
            final int gridX = (int) Math.round(vertex.x());
            final int gridZ = (int) Math.round(vertex.y());
            return region.maybeAt(gridX, gridZ);
        }
    }
}
