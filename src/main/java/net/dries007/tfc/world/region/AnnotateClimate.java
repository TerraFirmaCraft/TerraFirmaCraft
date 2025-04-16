/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public enum AnnotateClimate implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        for (final var point : context.region.points())
        {
            final int x = point.x;
            final int z = point.z;

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

            //Set rainfall variance by distance from west coast
            point.rainfallVariance = Mth.clampedMap(point.distanceToWestCoast + (float) context.generator().rainfallVarianceNoise.noise(x, z), 0f, 80f, -1, 1);

            // Calculate targets to bias towards
            final float biasTargetTemperature = Mth.lerp(bias, 5f, point.temperature);
            final float biasTargetRainfall = Mth.lerp(bias, Math.min(point.rainfall + 350f, 500f), point.rainfall);

            // Calculate influence magnitudes, and apply influence to targets

            // A constant influence magnitude is either too small to be noticeable, or so dominant that some climates are impossible on shores
            // Temp delta range is set so that some coasts can have immoderate influences, as if from polar/equatorial currents
            final float tempDelta = Mth.clampedMap((float) context.generator().oceanicInfluenceNoise.noise(x, z), -0.8f, 0.9f, -0.07f, 0.23f);
            final float oldTemp = point.temperature;
            point.temperature = Mth.lerp(tempDelta, oldTemp, biasTargetTemperature);

            // Rain delta is adjusted so that rainfall increase scales with temperature increase
            final float rainDelta = Mth.clampedMap(point.temperature - oldTemp, -2f, 2f, 0f, 0.25f);
            point.rainfall = Math.clamp(Mth.lerp(rainDelta, point.rainfall, biasTargetRainfall), 0, 500);

            //  Reduce rainfall variance near cell borders
            final float edgeBiasScale = Mth.clampedMap(point.distanceToEdge, 0, 12, 1, 0);
            point.rainfallVariance = Mth.lerp(edgeBiasScale, point.rainfallVariance, 0);
            point.rainfallVariance = Mth.clamp(point.rainfallVariance, -1, 1);
        }
    }
}
