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

            if (context.generator().settings.finiteContinents())
            {

                final int rainfallScale = context.generator().settings.rainfallConstant() != 0 ? 20000 : context.generator().settings.rainfallScale();
                final int temperatureScale = context.generator().settings.temperatureConstant() != 0 ? 20000 : context.generator().settings.temperatureScale();

                // keep temperature and rainfall falloff as independent functions since it won't always be square

                final float tempDistance = (Math.abs(gridToBlock(point.z) - temperatureScale / 2) / (temperatureScale * 1.2f));
                final float rainfallDistance = (Math.abs(gridToBlock(point.x)) / (rainfallScale * 1.2f));

                final float falloff = falloff(tempDistance) * falloff(rainfallDistance);

                continent *= falloff;
            }

            if (continent > 4.4)
            {
                point.setLand();
            }
        }
    }

    private static float falloff(float distance)
    {
        int order = 5;
        return (float) Mth.clamp(Math.pow(-distance, order) + 1f, 0f, 1f);
    }
}
