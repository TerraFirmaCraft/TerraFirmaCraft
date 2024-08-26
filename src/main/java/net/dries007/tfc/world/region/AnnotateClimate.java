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
        for (final var point : context.region.points())
        {
            // Climate is seeded with a base value based on noise
            // This keeps the large-scale climate which we want
            point.temperature = (float) context.generator().temperatureNoise.noise(point.x, point.z);
            point.rainfall = (float) context.generator().rainfallNoise.noise(point.x, point.z);

            // [0, 1], where higher = more inland
            final float bias;
            final float biasTargetVariance;
            if (point.land())
            {
                assert point.distanceToOcean >= 0;

                // Bias temperature by distance to ocean, using a basic rule:
                // Proximity to an ocean *increases* rainfall, and *normalizes* temperature, with the same bias in reverse.
                final float potentialBias = Mth.clampedMap(point.distanceToEdge, 2f, 6f, 0f, 1f);
                final float oceanProximityBias = Mth.clampedMap(point.distanceToOcean, 2f, 6f, 0f, 1f);

                bias = Math.min(potentialBias, oceanProximityBias);
                biasTargetVariance = Mth.clampedMap(point.distanceToWestCoast, 0f, 80f, -1, 1);
            }
            else
            {
                bias = 0;
                biasTargetVariance = 0;
            }

            // Calculate targets to bias towards
            final float biasTargetTemperature = Mth.lerp(bias, 5f, point.temperature);
            final float biasTargetRainfall = Mth.lerp(bias, Math.min(point.rainfall + 350f, 500f), point.rainfall);

            // And apply some influence towards those targets
            point.temperature = Mth.lerp(0.23f, point.temperature, biasTargetTemperature);
            point.rainfall = Mth.lerp(0.23f, point.rainfall, biasTargetRainfall);

            // Bias rainfall variance by distance from west coast
            point.rainfallVariance = Mth.lerp(1f, point.rainfallVariance, biasTargetVariance);

            // Reduce rainfall variance near cell borders
            final float edgeBiasScale = Mth.clampedMap(point.distanceToEdge, 0, 12, 1, 0);
            point.rainfallVariance = Mth.lerp(edgeBiasScale, point.rainfallVariance, 0);

            point.rainfallVariance = Mth.clamp(point.rainfallVariance + (float) context.generator().rainfallVarianceNoise.noise(point.x, point.z), -1, 1);
        }
    }
}
