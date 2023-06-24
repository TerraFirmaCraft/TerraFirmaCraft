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

public enum AnnotateBaseLandHeight implements RegionTask
{
    INSTANCE;

    private static final int ISLAND_SEED_DEPTH = 3;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.sizeX() * region.sizeZ());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntList islandQueue = new IntArrayList();
        final RandomSource random = context.random;

        // Annotate base land height using min(noise, distance from ocean)
        // Also seeds a queue of shore points for base ocean height, and island points
        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && point.land())
                {
                    // Base land height is a simple approximation of inland-ness, but with influence from distance to the edge
                    // We use this to place mountains along contours
                    point.baseLandHeight = point.distanceToOcean;
                    if (point.baseLandHeight > point.distanceToEdge)
                    {
                        point.baseLandHeight = (byte) (0.3f * point.baseLandHeight + 0.7f * point.distanceToEdge);
                    }

                    explored.set(index);

                    if (point.island())
                    {
                        point.baseOceanDepth = ISLAND_SEED_DEPTH;
                        islandQueue.add(index);
                    }
                    else
                    {
                        point.baseOceanDepth = 0;
                        queue.enqueue(index);
                    }
                }
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.data()[last];
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
                    final int next = region.offset(last, dx, dz);
                    if (next == -1)
                    {
                        continue;
                    }
                    final Region.Point point = region.data()[next];
                    if (point != null && !point.land() && point.baseOceanDepth == 0)
                    {
                        if (!explored.get(next))
                        {
                            if (random.nextInt(15) == 0)
                            {
                                // Not a true BFS, we have some 'cheat' points
                                // To preserve the nature of the BFS we enqueueFirst for these points, so they stay in the right batch
                                point.baseOceanDepth = lastPoint.baseOceanDepth;
                                queue.enqueueFirst(next);
                            }
                            else
                            {
                                point.baseOceanDepth = (byte) nextDepth;
                                queue.enqueue(next);
                            }
                        }
                    }
                    explored.set(next);
                }
            }
        }
    }
}
