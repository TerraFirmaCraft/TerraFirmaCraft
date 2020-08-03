/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

public enum BiomeWeightType
{
    NORMAL(weights -> {}), // No weight modifications
    OCEAN_IS_SHORE(weights -> weightTowardsShore(weights, BiomeLandType.OCEAN)), // Used by land to pull up oceans to shore level
    LAND_IS_SHORE(weights -> weightTowardsShore(weights, BiomeLandType.LAND)), // Used by oceans to pull down land to shore level
    FAVOR_MAJORITY(weights -> weightTowardsMajority(weights, 0.6)), // Favor the >50% weight, creates cliffs
    HEAVY_FAVOR_MAJORITY(weights -> weightTowardsMajority(weights, 0.4)); // Heavily favor the >50% weight, creates large cliffs

    private static void weightTowardsShore(Object2DoubleMap<TFCBiome> weights, BiomeLandType type)
    {
        Set<TFCBiome> removedBiomes = new HashSet<>();
        TFCBiome shoreBiome = TFCBiomes.SHORE.get();
        double shoreWeight = 0;
        for (Object2DoubleMap.Entry<TFCBiome> entry : weights.object2DoubleEntrySet())
        {
            BiomeLandType landType = entry.getKey().getVariants().getLandType();
            if (landType == BiomeLandType.SHORE)
            {
                shoreBiome = entry.getKey();
            }
            else if (landType == type)
            {
                shoreWeight += entry.getDoubleValue();
                removedBiomes.add(entry.getKey());
            }
        }
        weights.mergeDouble(shoreBiome, shoreWeight, Double::sum);
        removedBiomes.forEach(weights::removeDouble);
    }

    private static void weightTowardsMajority(Object2DoubleMap<TFCBiome> weights, double bias)
    {
        // Find the majority entry
        TFCBiome maxBiome = null;
        double maxWeight = 0;
        for (Object2DoubleMap.Entry<TFCBiome> entry : weights.object2DoubleEntrySet())
        {
            if (entry.getDoubleValue() > maxWeight)
            {
                maxWeight = entry.getDoubleValue();
                maxBiome = entry.getKey();
            }
        }

        // Re-weight every entry
        for (Object2DoubleMap.Entry<TFCBiome> entry : weights.object2DoubleEntrySet())
        {
            if (entry.getKey() == maxBiome)
            {
                entry.setValue(1 - bias * (1 - entry.getDoubleValue()));
            }
            else
            {
                entry.setValue(bias * entry.getDoubleValue());
            }
        }
    }

    private final Consumer<Object2DoubleMap<TFCBiome>> weightModifier;

    BiomeWeightType(Consumer<Object2DoubleMap<TFCBiome>> weightModifier)
    {
        this.weightModifier = weightModifier;
    }

    public void apply(Object2DoubleMap<TFCBiome> biomeWeights)
    {
        weightModifier.accept(biomeWeights);
    }
}
