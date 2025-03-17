/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;

public enum AddHotspots implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final double threshold = 0.65;
        final double expansionThreshold = 0.15;

        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();

        // If a location reaches a value of at least exceeding a threshold value, a hot spot is placed in the region
        for (final var point : region.points())
        {
            final Cellular2D.Cell cell = context.generator().plateRegionNoise.cell(point.x, point.z);
            final double edgeDist = Math.abs(cell.f1() - cell.f2());

            double val = context.generator().hotSpotIntensityNoise.noise(shift(point.x), shift(point.z));
            if (val > threshold && edgeDist > 0.05)
            {
                final byte age = (byte) (int) context.generator().hotSpotAgeNoise.noise(shift(point.x), shift(point.z));
                point.hotSpotAge = age;
                if (age != 4)
                    point.setLand();
                queue.enqueue(point.index);
            }
        }

        // From the above crater locations, the hotspots are extended outwards
        while (!queue.isEmpty())
        {
            final int index = queue.dequeueInt();
            final byte lastAge = region.atIndex(index).hotSpotAge;;

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final Region.Point next = region.atOffset(index, dx, dz);
                    if (next != null && next.hotSpotAge == 0)
                    {
                        if (context.generator().hotSpotIntensityNoise.noise(shift(next.x), shift(next.z)) > expansionThreshold)
                        {
                            queue.enqueue(next.index);
                            next.hotSpotAge = lastAge;
                            if (lastAge != 4)
                                next.setLand();
                        }
                        // This adds an extra layer outside where the hotspot exceeds the threshold as a buffer against oceans
                        else if (!next.land() && context.generator().hotSpotIntensityNoise.noise(shift(next.x) - dx, shift(next.z) - dz) > expansionThreshold)
                        {
                            // Do not set land on the outer layer
                            next.hotSpotAge = lastAge;
                            if (lastAge != 4)
                                next.setLand();
                        }
                    }
                }
            }
        }
    }

    // Use this when sampling noise from regional coordinates to sample the center of the region point
    public double shift(int point)
    {
        return point + 0.5;
    }
}