/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;

public enum AnnotateDistanceToOcean implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.sizeX() * region.sizeZ());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && !point.land())
                {
                    point.distanceToOcean = -1;
                    queue.enqueue(index);
                    explored.set(index);
                }
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.data()[last];
            final int nextDistance = lastPoint.distanceToOcean + 1;

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
                    if (point != null && point.land() && point.distanceToOcean == 0)
                    {
                        if (!lastPoint.land() && !point.island())
                        {
                            lastPoint.setShore(); // Mark as adjacent to land
                        }

                        if (!explored.get(next))
                        {
                            point.distanceToOcean = (byte) nextDistance;
                            queue.enqueue(next);
                        }
                    }
                    explored.set(next);
                }
            }
        }
    }
}
