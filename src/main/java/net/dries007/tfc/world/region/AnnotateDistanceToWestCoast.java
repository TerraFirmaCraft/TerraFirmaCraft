/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public enum AnnotateDistanceToWestCoast implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.atIndex(index);
                if (point != null)
                {
                    if (dx == 0)
                    {
                        point.distanceToWestCoast = 0;
                    }
                    else
                    {
                        final Region.Point lastCenterPoint = region.atIndex(index - 1);
                        if (lastCenterPoint == null)
                        {
                            //Sets a little starting bonus when a point in on the eastern border of a cell. The cusp will be smoothed out in the actual rain map
                            point.distanceToWestCoast = point.land() ? (byte) (25 + point.distanceToOcean) : 0;
                        }
                        else
                        {
                            final int lastCenterVal = lastCenterPoint.distanceToWestCoast;
                            if (!point.land())
                            {
                                //Ocean east of a shore will decrease in value faster than the land gains in value, and never below zero
                                point.distanceToWestCoast = (byte) Math.max(lastCenterVal - 2, 0);
                            }
                            else
                            {
                                int sum = 0;
                                final float scale = context.generator().settings.temperatureScale();
                                final float frequency = Units.GRID_WIDTH_IN_BLOCK / (2f * scale);
                                final float function = Math.abs(4f * frequency * point.z - 4f * Mth.floor(frequency * point.z + 0.75f)) - 1f;
                                final int start = -2 + (function > 0.1 ? 1 : 0);
                                final int end = 2 - (function < -0.1 ? 1 : 0);
                                //Can adjust the start and end point of this loop arbitrarily, just change the denominator of the average function too
                                for (int dz2 = start; dz2 <= end; dz2++)
                                {
                                    final Region.Point lastPoint = region.atOffset(point.index, -1, dz2);
                                    if (lastPoint != null)
                                    {
                                        sum = sum + lastPoint.distanceToWestCoast;
                                    }
                                    else
                                    {
                                        sum = sum + lastCenterVal;
                                    }
                                }
                                point.distanceToWestCoast = (byte) (Mth.ceil(sum / (1f + end - start)) + 1);
                            }
                        }
                    }
                }
            }
        }

        // For ocean tiles, we set values based on the value at the nearest coast
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();

        for (final var point : region.points())
        {
            if (point != null)
            {
                if (point.land())
                {
                    explored.set(point.index);
                    queue.enqueue(point.index);
                }
            }
        }

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            final int lastDistance = lastPoint.distanceToWestCoast;
            final int nextDistance = lastDistance + (lastDistance > 40 ? -1 : 1);

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    if (point != null && point.distanceToWestCoast == 0)
                    {
                        if (!explored.get(point.index))
                        {
                            point.distanceToWestCoast = (byte) nextDistance;
                            queue.enqueue(point.index);
                        }
                        explored.set(point.index);
                    }
                }
            }
        }
    }
}
