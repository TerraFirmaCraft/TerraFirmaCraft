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
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum AddMountains implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final RandomSource random = context.random;

        for (int attempt = 0, placed = 0; attempt < 40 && placed < 3; attempt++)
        {
            final @Nullable Region.Point origin = region.random(random);
            if (origin != null && origin.land())
            {
                // Attempt to construct a mountain range
                // We do this with a bit of a DFS / BFS hybrid - intentionally imprecise and random - across a contour of the base land height
                // Ranges at low altitudes (near ocean) get marked as oceanic ranges, where mid-high altitude ranges get marked as high altitude mountains.
                if (origin.baseLandHeight <= 1 || (origin.baseLandHeight >= 4 && origin.baseLandHeight <= 11))
                {
                    final IntSet range = placeRange(region, random, origin.index);
                    if (range.size() > 45)
                    {
                        range.forEach(index -> {
                            final Region.Point point = region.atIndex(index);

                            point.setMountain();
                            if (origin.baseLandHeight <= 2)
                            {
                                point.setCoastalMountain();
                            }
                        });
                        placed++;
                    }
                }
            }
        }
    }

    private IntSet placeRange(Region region, RandomSource random, int originIndex)
    {
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntSet range = new IntOpenHashSet();

        queue.enqueue(originIndex);
        explored.set(originIndex);
        range.add(originIndex);

        // So that low altitude ranges don't start at 0 altitude, now they can follow the [0, 1] contour
        final int originBaseLandHeight = Math.max(1, region.atIndex(originIndex).baseLandHeight);
        final int maxSize = 70 + random.nextInt(40);

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            if (range.size() > maxSize)
            {
                break;
            }

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);

                    // Only explore the contour within [-1, 0] of the origin
                    // The baseLandHeight > 2 || distanceToOcean < 3 is to avoid what should be coastal mountains diverting inland due to
                    // the presence of a cell edge causing an artificial low point.
                    if (point != null &&
                        point.land() &&
                        point.baseLandHeight >= originBaseLandHeight - 1 &&
                        point.baseLandHeight <= originBaseLandHeight + 1 &&
                        (point.baseLandHeight > 2 || point.distanceToOcean < 3) &&
                        !explored.get(point.index))
                    {
                        if (lastPoint.baseLandHeight != point.baseLandHeight)
                        {
                            queue.enqueue(point.index);
                        }
                        else
                        {
                            queue.enqueueFirst(point.index);
                        }
                        range.add(point.index);
                        explored.set(point.index);
                    }
                }
            }
        }

        return range;
    }
}
