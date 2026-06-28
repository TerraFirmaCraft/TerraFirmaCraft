/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

public enum AddContinentsAndSetOceanDepths implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        for (final var point : context.region.points())
        {
            final double perlinNoiseContinent = context.generator().continentNoise.noise(point.x, point.z);
            final double finiteWorldMultiplier = context.generator().continentFactor(point);
            final double tectonicFeatures = addRiftSeas(point);

            final double continent = (perlinNoiseContinent + tectonicFeatures) * finiteWorldMultiplier;
            if (continent > 4.4)
            {
                point.setLand();
            }
            // We set ocean depths here as well
            // Unset - 0
            // Reef - 1 (Set later)
            // Continental Shelf - 2
            // Oceanic Ridge - 3
            // Abyssal Plain - 4
            // Ocean Trench - 5
            else if (point.divergence > 0 && point.distanceToEdge < 2)
            {
                // Ocean ridges should override continental shelves in rifting areas
                point.oceanDepth = 3;
            }
            else if (continent > 3.3)
            {
                // Continental shelves unevenly fringing continents
                point.oceanDepth = 2;
            }
            else if (continent > 3 && point.divergence < 0)
            {
                // Trenches at edge of subduction zones
                point.oceanDepth = 5;
            }
            else if (point.distanceToEdge < 2 && !(point.divergence < 0 && continent > 2))
            {
                // Ocean ridges at cell borders
                // The divergence and continent checks here are to prevent them showing up in narrow oceans between subduction zones
                point.oceanDepth = 3;
            }
            else
            {
                // Abyssal plain elsewhere
                point.oceanDepth = 4;
            }
        }
    }

    private static double addRiftSeas(Region.Point point)
    {
        final double tectonicFeatures;
        if (point.distanceToEdge <= 5 && point.divergence > 0)
        {
            // Passive margin/rift valley
            tectonicFeatures = (5 - point.distanceToEdge) * -0.12;
        }
        else
        {
            tectonicFeatures = 0;
        }
        return tectonicFeatures;
    }
}
