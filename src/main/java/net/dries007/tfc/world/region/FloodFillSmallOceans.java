/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public enum FloodFillSmallOceans implements RegionTask
{
    INSTANCE;

    private static final int SMALL_OCEAN_FILL_THRESHOLD = 180;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.sizeX() * region.sizeZ());

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (!explored.get(index) && point != null && !point.land())
                {
                    floodFillSmallOcean(explored, index, region);
                }
            }
        }
    }

    private void floodFillSmallOcean(BitSet explored, int index, Region region)
    {
        final IntSet values = new IntOpenHashSet();
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        queue.enqueue(index);
        values.add(index);
        boolean unbounded = false;
        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final int next = region.offset(last, dx, dz);
                    if (next == -1)
                    {
                        unbounded = true;
                        continue;
                    }
                    final Region.Point point = region.data()[next];
                    if (point == null)
                    {
                        unbounded = true;
                        continue;
                    }
                    if (point.land() || explored.get(next))
                    {
                        continue;
                    }

                    explored.set(next);
                    queue.enqueue(next);
                    values.add(next);
                }
            }
        }

        if (values.size() < SMALL_OCEAN_FILL_THRESHOLD && !unbounded)
        {
            values.forEach(i -> region.data()[i].setLand());
        }
    }
}
