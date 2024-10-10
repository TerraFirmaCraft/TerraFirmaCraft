/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.dries007.tfc.world.settings.RockSettings;

public enum KarstSurfaceRocks implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final RegionGenerator regionGenerator = context.generator();

        for (final var point : region.points())
        {
            final RockSettings surfaceRock = regionGenerator.getSurfaceRock(point);
            point.isSurfaceRockKarst = surfaceRock.karst().isPresent() ? surfaceRock.karst().get() : false;
        }
    }
}
