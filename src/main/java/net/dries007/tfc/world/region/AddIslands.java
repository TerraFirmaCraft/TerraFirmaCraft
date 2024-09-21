/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.RandomSource;

import static net.dries007.tfc.world.region.AddContinents.*;
import static net.dries007.tfc.world.region.Units.*;

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
            Region.Point point = region.random(random);
            if (point == null)
            {
                continue;
            }

            if (context.generator().settings.finiteContinents())
            {
                final int rainfallScale = context.generator().settings.rainfallConstant() != 0 ? 20000 : context.generator().settings.rainfallScale();
                final int temperatureScale = context.generator().settings.temperatureConstant() != 0 ? 20000 : context.generator().settings.temperatureScale();

                final float maxX = rainfallScale;
                final float maxZ = temperatureScale + temperatureScale * 0.5f;
                final float minX = -rainfallScale;
                final float minZ = -(temperatureScale - temperatureScale * 0.5f);

                final int pointX = gridToBlock(point.x);
                final int pointZ = gridToBlock(point.z);

                if (!(pointX < maxX && pointX > minX
                    && pointZ < maxZ && pointZ > minZ))
                {
                    continue;
                }
            }

            if (!point.land() && !point.shore() && point.distanceToEdge > 2)
            {
                // Place a small island chain
                for (int island = 0; island < 12; island++)
                {
                    point.setLand();
                    point.setIsland();
                    point = region.at(
                        point.x + random.nextInt(4) - random.nextInt(4),
                        point.z + random.nextInt(4) - random.nextInt(4)
                    );
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
