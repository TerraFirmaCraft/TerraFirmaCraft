/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import net.minecraft.util.RandomSource;

public enum AnnotateBiomeAltitude implements RegionTask
{
    INSTANCE;

    public static final int WIDTH = 4;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        // Altitude is a more direct measure of what points map to what biomes
        // Mountains - 3
        // High - 2
        // Mid - 1
        // Low - 0
        // Near Island -1

        // Begin a basic BFS out from mountain blobs, stepping down
        final Region region = context.region;
        final RandomSource random = context.random;
        final BitSet explored = new BitSet(region.sizeX() * region.sizeZ());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && point.land() && point.mountain())
                {
                    point.biomeAltitude = 3 * WIDTH;
                    queue.enqueue(index);
                    explored.set(index);
                }
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.data()[last];
            final int nextAltitude = lastPoint.biomeAltitude - 1;
            if (nextAltitude < 0)
            {
                continue;
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
                    if (point != null && point.land() && point.biomeAltitude == 0 && !explored.get(next))
                    {
                        // Minor non-uniformity, makes regions a bit messier
                        if (random.nextInt(13) == 0 && lastPoint.biomeAltitude != 3 * WIDTH)
                        {
                            point.biomeAltitude = lastPoint.biomeAltitude;
                            queue.enqueueFirst(next);
                        }
                        else
                        {
                            point.biomeAltitude = (byte) nextAltitude;
                            queue.enqueue(next);
                        }
                    }
                    explored.set(next);
                }
            }
        }

        // Run another pass over the entire region, this time raising land from low -> mid
        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null && point.land() && point.discreteBiomeAltitude() == 0 && point.baseLandHeight >= 4)
                {
                    if (point.discreteBiomeAltitude() == 0 && point.baseLandHeight >= 4)
                    {
                        point.biomeAltitude = WIDTH;
                    }
                    if (point.discreteBiomeAltitude() == 1 && point.baseLandHeight >= 11)
                    {
                        point.biomeAltitude = 2 * WIDTH;
                    }
                }
            }
        }
    }
}
