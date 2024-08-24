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
import org.jetbrains.annotations.Nullable;

public enum FloodFillSmallOceans implements RegionTask
{
    INSTANCE;

    private static final int SMALL_OCEAN_FILL_THRESHOLD = 180;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.size());

        for (final var point : region.points())
        {
            if (point != null && !point.land() && !explored.get(point.index))
            {
                floodFillSmallOcean(explored, point.index, region);
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
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    if (point == null)
                    {
                        unbounded = true;
                        continue;
                    }
                    if (point.land() || explored.get(point.index))
                    {
                        continue;
                    }

                    explored.set(point.index);
                    queue.enqueue(point.index);
                    values.add(point.index);
                }
            }
        }

        if (values.size() < SMALL_OCEAN_FILL_THRESHOLD && !unbounded)
        {
            values.forEach(i -> region.atIndex(i).setLand());
        }
    }
}
