/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum AnnotateBaseLandHeight implements RegionTask
{
    INSTANCE;

    private static final int ISLAND_SEED_DEPTH = 3;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntList islandQueue = new IntArrayList();
        final RandomSource random = context.random;

        // Annotate base land height using min(noise, distance from ocean)
        // Also seeds a queue of shore points for base ocean height, and island points
        for (final var point : region.points())
        {
            if (point.land())
            {
                // Base land height is a simple approximation of inland-ness, but with influence from distance to the edge
                // We use this to place mountains along contours
                point.baseLandHeight = point.distanceToOcean;
                if (point.baseLandHeight > point.distanceToEdge)
                {
                    point.baseLandHeight = (byte) (0.3f * point.baseLandHeight + 0.7f * point.distanceToEdge);
                }

                explored.set(point.index);

                if (point.island())
                {
                    point.baseOceanDepth = ISLAND_SEED_DEPTH;
                    islandQueue.add(point.index);
                }
                else
                {
                    point.baseOceanDepth = 0;
                    queue.enqueue(point.index);
                }
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            final int nextDepth = lastPoint.baseOceanDepth + 1;

            if (nextDepth == ISLAND_SEED_DEPTH && !islandQueue.isEmpty())
            {
                // Seed with island queue points now - this is a hack to avoid using a priority queue
                islandQueue.forEach(queue::enqueue);
                islandQueue.clear();
            }

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    if (point != null && !point.land() && point.baseOceanDepth == 0)
                    {
                        if (!explored.get(point.index))
                        {
                            if (random.nextInt(15) == 0)
                            {
                                // Not a true BFS, we have some 'cheat' points
                                // To preserve the nature of the BFS we enqueueFirst for these points, so they stay in the right batch
                                point.baseOceanDepth = lastPoint.baseOceanDepth;
                                queue.enqueueFirst(point.index);
                            }
                            else
                            {
                                point.baseOceanDepth = (byte) nextDepth;
                                queue.enqueue(point.index);
                            }
                        }
                        explored.set(point.index);
                    }
                }
            }
        }
    }
}
