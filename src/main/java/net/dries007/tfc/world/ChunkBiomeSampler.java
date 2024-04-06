/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.function.Function;
import java.util.function.ToIntFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.world.biome.BiomeBlendType;
import net.dries007.tfc.world.noise.Kernel;

/**
 * Contains static methods related to biome or height sampling.
 */
public final class ChunkBiomeSampler
{
    public static final Kernel KERNEL_9x9 = Kernel.create((x, z) -> 0.0211640211641D * (1 - 0.03125D * (z * z + x * x)), 4);

    /**
     * @param pos           The target chunk pos.
     * @param biomeSampler  A sampler for biomes, in block coordinates.
     * @param groupFunction A function to access a {@link BiomeBlendType} from a {@link Biome}.
     * @return A 7x7 array of sampled biome weights, at quart pos resolution, where the (0, 0) index aligns to the (-1, -1) quart position relative to the target chunk.
     */
    public static <T> Object2DoubleMap<T>[] sampleBiomes(ChunkPos pos, Sampler<T> biomeSampler, Function<T, BiomeBlendType> groupFunction)
    {
        // First, sample biomes at chunk distance, in a 4x4 grid centered on the target chunk.
        // These are used to build the large-scale biome blending radius
        final Object2DoubleMap<T>[] chunkBiomeWeightArray = newWeightArray(4 * 4);
        final int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ(); // Block coordinates
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                // x, z = 0, 0 is the -1, -1 chunk relative to chunkX, chunkZ
                final Object2DoubleMap<T> chunkBiomeWeight = new Object2DoubleOpenHashMap<>();
                chunkBiomeWeightArray[x | (z << 2)] = chunkBiomeWeight;
                sampleBiomesAtPositionWithKernel(chunkBiomeWeight, biomeSampler, KERNEL_9x9, 4, chunkX, chunkZ, x - 1, z - 1);
            }
        }

        // A 7x7 grid, in quart positions relative to the target chunk, where (1, 1) is the target chunk origin.
        final Object2DoubleMap<T>[] quartBiomeWeightArray = newWeightArray(7 * 7);
        final Object2DoubleMap<T> chunkBiomeWeight = new Object2DoubleOpenHashMap<>();

        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                // Reset
                final Object2DoubleMap<T> quartBiomeWeight = new Object2DoubleOpenHashMap<>();
                chunkBiomeWeight.clear();

                sampleBiomesAtPositionWithKernel(quartBiomeWeight, biomeSampler, KERNEL_9x9, 2, chunkX, chunkZ, x - 1, z - 1);

                // Calculate contribution from the four corners of the 16x16 grid. First, calculate the current grid cell coordinates.
                final int x1 = chunkX + ((x - 1) << 2); // Block coordinates
                final int z1 = chunkZ + ((z - 1) << 2);

                final int coordX = x1 >> 4; // Chunk coordinates
                final int coordZ = z1 >> 4;

                final double lerpX = (x1 - (coordX << 4)) * (1 / 16d); // Deltas, in the range [0, 1)
                final double lerpZ = (z1 - (coordZ << 4)) * (1 / 16d);

                final int index16X = ((x1 - chunkX) >> 4) + 1; // Index into chunkBiomeWeightArray
                final int index16Z = ((z1 - chunkZ) >> 4) + 1;

                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[index16X | (index16Z << 2)], (1 - lerpX) * (1 - lerpZ));
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[(index16X + 1) | (index16Z << 2)], lerpX * (1 - lerpZ));
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[index16X | ((index16Z + 1) << 2)], (1 - lerpX) * lerpZ);
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[(index16X + 1) | ((index16Z + 1) << 2)], lerpX * lerpZ);

                // Compose chunk weights -> wide quart weights.
                composeSampleWeights(quartBiomeWeight, chunkBiomeWeight, biome -> {
                    final BiomeBlendType group = groupFunction.apply(biome);
                    return group.ordinal();
                }, BiomeBlendType.SIZE);

                quartBiomeWeightArray[x + 7 * z] = quartBiomeWeight;
            }
        }
        return quartBiomeWeightArray;
    }

    public static <T> void sampleBiomesColumn(Object2DoubleMap<T> accumulator, Object2DoubleMap<T>[] corners, int localX, int localZ)
    {
        final int index4X = (localX >> 2) + 1;
        final int index4Z = (localZ >> 2) + 1;

        final double lerpX = (localX - ((localX >> 2) << 2)) * (1 / 4d);
        final double lerpZ = (localZ - ((localZ >> 2) << 2)) * (1 / 4d);

        accumulator.clear();
        sampleBiomesCornerContribution(accumulator, corners[index4X + index4Z * 7], (1 - lerpX) * (1 - lerpZ));
        sampleBiomesCornerContribution(accumulator, corners[(index4X + 1) + index4Z * 7], lerpX * (1 - lerpZ));
        sampleBiomesCornerContribution(accumulator, corners[index4X + (index4Z + 1) * 7], (1 - lerpX) * lerpZ);
        sampleBiomesCornerContribution(accumulator, corners[(index4X + 1) + (index4Z + 1) * 7], lerpX * lerpZ);
    }

    private static <T> void sampleBiomesCornerContribution(Object2DoubleMap<T> accumulator, Object2DoubleMap<T> corner, double t)
    {
        if (t > 0)
        {
            for (Object2DoubleMap.Entry<T> entry : corner.object2DoubleEntrySet())
            {
                accumulator.mergeDouble(entry.getKey(), entry.getDoubleValue() * t, Double::sum);
            }
        }
    }

    private static <T> void sampleBiomesAtPositionWithKernel(Object2DoubleMap<T> weights, Sampler<T> biomeSampler, Kernel kernel, int kernelBits, int chunkX, int chunkZ, int xOffsetInKernelBits, int zOffsetInKernelBits)
    {
        final int kernelRadius = kernel.radius();
        final int kernelWidth = kernel.width();
        for (int dx = -kernelRadius; dx <= kernelRadius; dx++)
        {
            for (int dz = -kernelRadius; dz <= kernelRadius; dz++)
            {
                final double weight = kernel.values()[(dx + kernelRadius) + (dz + kernelRadius) * kernelWidth];
                final int blockX = chunkX + ((xOffsetInKernelBits + dx) << kernelBits); // Block positions
                final int blockZ = chunkZ + ((zOffsetInKernelBits + dz) << kernelBits);
                final T biome = biomeSampler.get(blockX, blockZ);
                weights.mergeDouble(biome, weight, Double::sum);
            }
        }
    }

    /**
     * Composes two levels of sampled weights. It takes two maps of two different resolutions, and re-weights the higher resolution one by replacing specific groups of samples with the respective weights from the lower resolution map.
     * Each element of the higher resolution map is replaced with a proportional average of the same group which is present in the lower resolution map.
     * This has the effect of blending specific groups at closer distances than others, allowing for both smooth and sharp biome transitions.
     * <p>
     * Example:
     * - Low resolution: 30% Plains, 40% Mountains, 30% Hills, 10% River
     * - High resolution: 60% Plains, 40% River
     * - Groups are "River" and "Not River"
     * - For each element in the high resolution map:
     * - 60% Plains: Group "Not River", and is replaced with 60% * (30% Plains, 40% Mountains, 30% Hills) / 90%
     * - 50% River: Group "River", which is replaced with 40% * (10% River) / 10%
     * - Result: 18% Plains, 24% Mountains, 18% Hills, 40% River
     */
    private static <T> void composeSampleWeights(Object2DoubleMap<T> weightMap, Object2DoubleMap<T> groupWeightMap, ToIntFunction<T> groupFunction, int groups)
    {
        // First, we need to calculate the maximum weight per group
        double[] maxWeights = new double[groups];
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1)
            {
                maxWeights[group] += entry.getDoubleValue();
            }
        }

        // Then, we iterate through the smaller weight map and identify the actual weight that needs to be replaced with each group
        double[] actualWeights = new double[groups];
        ObjectIterator<Object2DoubleMap.Entry<T>> iterator = weightMap.object2DoubleEntrySet().iterator();
        while (iterator.hasNext())
        {
            Object2DoubleMap.Entry<T> entry = iterator.next();
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1)
            {
                actualWeights[group] += entry.getDoubleValue();
                iterator.remove();
            }
        }

        // Finally, insert the weights for each group as a portion of the actual weight
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1 && actualWeights[group] > 0 && maxWeights[group] > 0)
            {
                weightMap.put(entry.getKey(), entry.getDoubleValue() * actualWeights[group] / maxWeights[group]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Object2DoubleMap<T>[] newWeightArray(int size)
    {
        return (Object2DoubleMap<T>[]) new Object2DoubleMap[size]; // Avoid generic array warnings / errors
    }
}
