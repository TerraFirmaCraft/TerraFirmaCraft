/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.RandomSource;

public enum AddIslands implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final RandomSource random = context.random;

        for (int attempt = 0, placed = 0; attempt < 130 && placed < 15; attempt++)
        {
            int x = region.minX() + random.nextInt(region.sizeX());
            int z = region.minZ() + random.nextInt(region.sizeZ());

            Region.Point point = region.maybeAt(x, z);
            if (point != null && !point.land() && !point.shore() && point.distanceToEdge > 2)
            {
                // Place a small island chain
                for (int island = 0; island < 12; island++)
                {
                    point.setLand();
                    point.setIsland();

                    x += random.nextInt(4) - random.nextInt(4);
                    z += random.nextInt(4) - random.nextInt(4);

                    point = region.maybeAt(x, z);
                    if (point == null || (point.land() && !point.island()) || point.distanceToEdge <= 2)
                    {
                        break;
                    }
                }
                placed += 1;
            }
        }
    }
}
