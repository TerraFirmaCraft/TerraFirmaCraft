/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import org.jetbrains.annotations.Nullable;

public enum AnnotateDistanceToOcean implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();

        for (final var point : region.points())
        {
            if (point != null && !point.land())
            {
                point.distanceToOcean = -1;
                queue.enqueue(point.index);
                explored.set(point.index);
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            final int nextDistance = lastPoint.distanceToOcean + 1;

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    if (point != null && point.land() && point.distanceToOcean == 0)
                    {
                        if (!lastPoint.land() && !point.island())
                        {
                            lastPoint.setShore(); // Mark as adjacent to land
                        }

                        if (!explored.get(point.index))
                        {
                            point.distanceToOcean = (byte) nextDistance;
                            queue.enqueue(point.index);
                        }
                        explored.set(point.index);
                    }
                }
            }
        }
    }
}
