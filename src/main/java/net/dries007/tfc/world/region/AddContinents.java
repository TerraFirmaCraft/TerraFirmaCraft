/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

public enum AddContinents implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        for (final var point : context.region.points())
        {
            final double continent = context.generator().continentNoise.noise(point.x, point.z);
            if (continent > 4.4)
            {
                point.setLand();
            }
        }
    }
}
