/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.Mth;

public enum AnnotateClimate implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        for (int x = region.minX(); x <= region.maxX(); x++)
        {
            for (int z = region.minZ(); z <= region.maxZ(); z++)
            {
                final Region.Point point = region.maybeAt(x, z);
                if (point != null)
                {
                    // Climate is seeded with a base value based on noise
                    // This keeps the large-scale climate which we want
                    point.temperature = (float) context.generator().temperatureNoise.noise(x, z);
                    point.rainfall = (float) context.generator().rainfallNoise.noise(x, z);

                    // [0, 1], where higher = more inland
                    final float bias;
                    if (point.land())
                    {
                        assert point.distanceToOcean >= 0;

                        // Bias temperature by distance to ocean, using a basic rule:
                        // Proximity to an ocean *increases* rainfall, and *normalizes* temperature, with the same bias in reverse.
                        final float potentialBias = Mth.clampedMap(point.distanceToEdge, 2f, 6f, 0f, 1f);
                        final float oceanProximityBias = Mth.clampedMap(point.distanceToOcean, 2f, 6f, 0f, 1f);

                        bias = Math.min(potentialBias, oceanProximityBias);
                    }
                    else
                    {
                        bias = 0;
                    }

                    // Calculate targets to bias towards
                    final float biasTargetTemperature = Mth.lerp(bias, 5f, point.temperature);
                    final float biasTargetRainfall = Mth.lerp(bias, Math.min(point.rainfall + 350f, 500f), point.rainfall);

                    // And apply some influence towards those targets
                    point.temperature = Mth.lerp(0.23f, point.temperature, biasTargetTemperature);
                    point.rainfall = Mth.lerp(0.23f, point.rainfall, biasTargetRainfall);
                }
            }
        }
    }
}
