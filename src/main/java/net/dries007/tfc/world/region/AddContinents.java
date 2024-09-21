/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.Mth;

import static net.dries007.tfc.world.region.Units.*;

public enum AddContinents implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        for (final var point : context.region.points())
        {
            double continent = context.generator().continentNoise.noise(point.x, point.z);

            // keep temperature and rainfall falloff as independent functions since it won't always be square

            float tempDistance = (float) Math.abs(gridToBlock(point.z) - context.generator().settings.temperatureScale() / 2) / (context.generator().settings.temperatureScale() * 1.2f);
            float rainfallDistance = (float) Math.abs(gridToBlock(point.x)) / (context.generator().settings.rainfallScale() * 1.2f);

            // little logarithmic falloff function

            float falloff = falloff(tempDistance) * falloff(rainfallDistance);

            continent *= falloff;

            if (continent > 4.4)
            {
                point.setLand();
            }
        }
    }

    public static float falloff(float distance)
    {
        return Math.abs(Mth.clamp((0.16f * (float) Math.log((-(distance) + 1.02f)) + 1f), 0, 1));
    }
}
