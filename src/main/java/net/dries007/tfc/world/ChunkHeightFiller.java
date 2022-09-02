/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Map;

import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.world.biome.BiomeExtension;

/**
 * A high level stateless abstraction for height and biome sampling.
 * Allow structures to sample height during {@link net.minecraft.world.level.chunk.ChunkGenerator#getBaseHeight(int, int, Heightmap.Types, LevelHeightAccessor)}.
 */
public class ChunkHeightFiller
{
    protected final Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers; // Biome -> Noise Samplers
    protected final Object2DoubleMap<BiomeNoiseSampler> columnBiomeNoiseSamplers; // Per column weighted map of biome noises samplers

    protected final Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights; // 7x7 array of biome weights, at quart pos resolution
    protected final Object2DoubleMap<BiomeExtension> biomeWeights1; // Local biome weights, for individual column adjustment

    public ChunkHeightFiller(Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers, Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights)
    {
        this.biomeNoiseSamplers = biomeNoiseSamplers;
        this.columnBiomeNoiseSamplers = new Object2DoubleOpenHashMap<>();
        this.sampledBiomeWeights = sampledBiomeWeights;
        this.biomeWeights1 = new Object2DoubleOpenHashMap<>();
    }

    /**
     * Samples the height at a specific location with no side effects.
     *
     * @param blockX The block X.
     * @param blockZ The block Z.
     * @return The approximate height of the world at that location.
     */
    public double sampleHeight(int blockX, int blockZ)
    {
        prepareColumnBiomeWeights(blockX & 15, blockZ & 15);
        return sampleColumnHeightAndBiome(biomeWeights1, blockX, blockZ, false);
    }

    /**
     * Initializes {@link #biomeWeights1} from the sampled biome weights
     *
     * @param localX The chunk local X, in [0, 16)
     * @param localZ The chunk local Z, in [0, 16)
     */
    protected void prepareColumnBiomeWeights(int localX, int localZ)
    {
        final int index4X = (localX >> 2) + 1;
        final int index4Z = (localZ >> 2) + 1;

        final double lerpX = (localX - ((localX >> 2) << 2)) * (1 / 4d);
        final double lerpZ = (localZ - ((localZ >> 2) << 2)) * (1 / 4d);

        biomeWeights1.clear();
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + index4Z * 7], (1 - lerpX) * (1 - lerpZ));
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + index4Z * 7], lerpX * (1 - lerpZ));
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + (index4Z + 1) * 7], (1 - lerpX) * lerpZ);
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + (index4Z + 1) * 7], lerpX * lerpZ);
    }

    /**
     * For a given (x, z) position, samples the provided biome weight map to calculate the height at that location, and the biome
     *
     * @param blockX The block x value
     * @param blockZ The block z value
     * @param updateArrays In the stateful implementation of this class, if {@link #afterSampleColumnHeightAndBiome(Object2DoubleMap, BiomeExtension, double)} should be called.
     * @return The maximum height at this location
     */
    protected double sampleColumnHeightAndBiome(Object2DoubleMap<BiomeExtension> biomeWeights, int blockX, int blockZ, boolean updateArrays)
    {
        columnBiomeNoiseSamplers.clear();

        // Requires the column to be initialized (just x/z)
        double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
        double riverWeight = 0, shoreWeight = 0;
        BiomeExtension biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0; // Partition on biome type

        BiomeExtension oceanicBiomeAt = null;
        double oceanicWeight = 0, maxOceanicWeight = 0; // Partition on ocean/non-ocean or water type.

        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            final double weight = entry.getDoubleValue();
            final BiomeExtension variants = entry.getKey();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(variants);

            if (columnBiomeNoiseSamplers.containsKey(sampler))
            {
                columnBiomeNoiseSamplers.mergeDouble(sampler, weight, Double::sum);
            }
            else
            {
                sampler.setColumn(blockX, blockZ);
                columnBiomeNoiseSamplers.put(sampler, weight);
            }

            double height = weight * sampler.height();
            totalHeight += height;

            // Partition into river / shore / normal for standard biome transformations
            if (variants.isRiver())
            {
                riverHeight += height;
                riverWeight += weight;
                if (maxRiverWeight < weight)
                {
                    riverBiomeAt = entry.getKey();
                    maxRiverWeight = weight;
                }
            }
            else if (variants.isShore())
            {
                shoreHeight += height;
                shoreWeight += weight;
                if (maxShoreWeight < weight)
                {
                    shoreBiomeAt = entry.getKey();
                    maxShoreWeight = weight;
                }
            }
            else if (maxNormalWeight < weight)
            {
                normalBiomeAt = entry.getKey();
                maxNormalWeight = weight;
            }

            // Also record oceanic biome types
            if (variants.isSalty())
            {
                oceanicWeight += weight;
                if (maxOceanicWeight < weight)
                {
                    oceanicBiomeAt = entry.getKey();
                    maxOceanicWeight = weight;
                }
            }
        }

        double actualHeight = totalHeight;
        if (riverWeight > 0.6 && riverBiomeAt != null)
        {
            // Primarily river biomes.
            // Based on the oceanic weight, we may apply a modifier which scales rivers down, and creates sharp cliffs near river borders.
            // If oceanic weight is high, this effect is ignored, and we intentionally weight towards the oceanic biome.
            double aboveWaterDelta = Mth.clamp(actualHeight - riverHeight / riverWeight, 0, 20);
            double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
            double actualHeightWithRiverContribution = riverHeight / riverWeight + adjustedAboveWaterDelta;

            // Contribution of ocean type biomes to the 'normal' weight.
            double normalWeight = 1 - riverWeight - shoreWeight;
            double oceanicContribution = Mth.clamp(oceanicWeight == 0 || normalWeight == 0 ? 0 : oceanicWeight / normalWeight, 0, 1);
            if (oceanicContribution < 0.5)
            {
                actualHeight = Mth.lerp(2 * oceanicContribution, actualHeightWithRiverContribution, actualHeight);
                biomeAt = riverBiomeAt;
            }
            else
            {
                // Consider this primarily an oceanic weight area, in biome only. Do not adjust the nominal height.
                biomeAt = oceanicBiomeAt;
            }
        }
        else if (riverWeight > 0 && normalBiomeAt != null)
        {
            double adjustedRiverWeight = 0.6 * riverWeight;
            actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight)) + riverHeight * (adjustedRiverWeight / riverWeight);

            biomeAt = normalBiomeAt;
        }
        else if (normalBiomeAt != null)
        {
            biomeAt = normalBiomeAt;
        }

        if ((shoreWeight > 0.6 || maxShoreWeight > maxNormalWeight) && shoreBiomeAt != null)
        {
            // Flatten beaches above a threshold, creates cliffs where the beach ends
            double aboveWaterDelta = actualHeight - shoreHeight / shoreWeight;
            if (aboveWaterDelta > 0)
            {
                if (aboveWaterDelta > 20)
                {
                    aboveWaterDelta = 20;
                }
                double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
                actualHeight = shoreHeight / shoreWeight + adjustedAboveWaterDelta;
            }
            biomeAt = shoreBiomeAt;
        }

        assert biomeAt != null;
        if (updateArrays)
        {
            afterSampleColumnHeightAndBiome(biomeWeights, biomeAt, actualHeight);
        }
        return actualHeight;
    }

    protected void afterSampleColumnHeightAndBiome(Object2DoubleMap<BiomeExtension> biomeWeights, BiomeExtension biomeAt, double actualHeight) {}
}
