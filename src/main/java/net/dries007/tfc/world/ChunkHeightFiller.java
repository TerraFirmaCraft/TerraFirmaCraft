/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;

/**
 * A high level stateless abstraction for height and biome sampling.
 * Allow structures to sample height during {@link net.minecraft.world.level.chunk.ChunkGenerator#getBaseHeight(int, int, Heightmap.Types, LevelHeightAccessor, RandomState)}.
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
        double totalHeight = 0, shoreHeight = 0;
        double shoreWeight = 0;
        BiomeExtension biomeAt, normalBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxShoreWeight = 0; // Partition on biome type

        double maxOceanicWeight = 0; // Partition on ocean/non-ocean or water type.

        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            final double weight = entry.getDoubleValue();
            final BiomeExtension biome = entry.getKey();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(biome);

            assert sampler != null : "Non-existent sampler for biome: " + biome.key();

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

            if (biome == TFCBiomes.SHORE)
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
            if (biome.isSalty())
            {
                if (maxOceanicWeight < weight)
                {
                    maxOceanicWeight = weight;
                }
            }
        }

        double actualHeight = totalHeight;
        biomeAt = normalBiomeAt;

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

    protected double afterSampleColumnHeightAndBiome(Object2DoubleMap<BiomeExtension> biomeWeights, BiomeExtension biomeAt, double actualHeight)
    {
        return actualHeight;
    }
}
