/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.function.Function;

import net.minecraft.util.Util;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

/**
 * Used to sample biomes within a chunk region. This performs three main functions:
 * 1) Efficient sampling - biomes at the same coordinate are not sampled more than once per chunk, they are built into arrays and initialized as necessary
 * 2) Low resolution sampling - biomes are sampled at low resolutions to allow biomes to influence noise generation from larger distances
 * 3) Linear interpolation - when sampling at lower resolutions, the weight is computed as a lerp'd area from the weights built at each of the corners. This is done in one pass over a 10x10 grid instead of a 9x9 for a single position.
 */
public class ChunkArraySampler
{
    public static final int CHUNK_SIZE = 16;
    public static final int SAMPLE_WIDTH = 4;
    public static final int CHUNK_SQUARE = CHUNK_SIZE + 2 * SAMPLE_WIDTH;
    public static final int SAMPLE_SQUARE = SAMPLE_WIDTH * 2 + 1;

    public static final double[] WEIGHT_FIELD = Util.make(new double[9 * 9], array ->
    {
        // Parabolic field with total summed area equal to 1
        for (int x = 0; x < 9; x++)
        {
            for (int z = 0; z < 9; z++)
            {
                array[x + 9 * z] = 0.0211640211641D * (1 - 0.03125D * ((z - 4) * (z - 4) + (x - 4) * (x - 4)));
            }
        }
    });

    /**
     * Builds a sampled array, for direct resolution
     */
    public static <T> T[] fillSampledArray(T[] array, CoordinateAccessor<T> accessor)
    {
        if (array.length != CHUNK_SQUARE * CHUNK_SQUARE)
        {
            throw new IllegalArgumentException("Array input is not sized correctly, should be " + CHUNK_SQUARE);
        }
        for (int i = 0; i < CHUNK_SQUARE; i++)
        {
            for (int j = 0; j < CHUNK_SQUARE; j++)
            {
                array[i + CHUNK_SQUARE * j] = accessor.get((i - SAMPLE_WIDTH), (j - SAMPLE_WIDTH));
            }
        }
        return array;
    }

    /**
     * Builds a sampled array, with low resolution sampling.
     */
    public static <T> T[] fillSampledArray(T[] array, CoordinateAccessor<T> accessor, int resolutionBits)
    {
        if (resolutionBits == 0)
        {
            throw new IllegalArgumentException("Call fillSampledArray(T[], CoordinateAccessor<T>) instead, it will build the correct size array for direct sampling");
        }
        int arrayWidth = (CHUNK_SIZE >> resolutionBits) + SAMPLE_SQUARE;
        if (array.length != arrayWidth * arrayWidth)
        {
            throw new IllegalArgumentException("Array input is not sized correctly, should be " + arrayWidth * arrayWidth);
        }
        for (int i = 0; i < arrayWidth; i++)
        {
            for (int j = 0; j < arrayWidth; j++)
            {
                array[i + arrayWidth * j] = accessor.get((i - SAMPLE_WIDTH) << resolutionBits, (j - SAMPLE_WIDTH) << resolutionBits);
            }
        }
        return array;
    }

    public static <T> void fillSampledWeightMap(T[] sampledArray, Object2DoubleMap<T> weightMap, int localX, int localZ)
    {
        weightMap.clear();
        for (int i = 0; i < SAMPLE_SQUARE; i++)
        {
            for (int j = 0; j < SAMPLE_SQUARE; j++)
            {
                double weight = WEIGHT_FIELD[i + SAMPLE_SQUARE * j];
                weightMap.mergeDouble(sampledArray[(i + localX) + CHUNK_SQUARE * (j + localZ)], weight, Double::sum);
            }
        }
    }

    public static <T> void fillSampledWeightMap(T[] sampledArray, Object2DoubleMap<T> weightMap, int resolutionBits, int localX, int localZ)
    {
        if (resolutionBits == 0)
        {
            throw new IllegalArgumentException("Call fillSampledWeightMap(T[], Object2DoubleMap<T>, int, int) instead, it will use the correct size array for direct sampling");
        }
        int coordX = localX >> resolutionBits, coordZ = localZ >> resolutionBits;
        int deltaMax = 1 << resolutionBits;
        float deltaX = ((float) localX - (coordX << resolutionBits)) / deltaMax, deltaZ = ((float) localZ - (coordZ << resolutionBits)) / deltaMax;
        int arrayWidth = (CHUNK_SIZE >> resolutionBits) + SAMPLE_SQUARE;
        if (sampledArray.length != arrayWidth * arrayWidth)
        {
            throw new IllegalArgumentException("Array was of size " + sampledArray.length + " but expected the side length to be " + arrayWidth);
        }
        weightMap.clear();
        for (int i = 0; i < SAMPLE_SQUARE; i++)
        {
            for (int j = 0; j < SAMPLE_SQUARE; j++)
            {
                double weight = WEIGHT_FIELD[i + SAMPLE_SQUARE * j];
                weightMap.mergeDouble(sampledArray[(i + (coordX + 1)) + arrayWidth * (j + (coordZ + 1))], weight * deltaX * deltaZ, Double::sum);
                weightMap.mergeDouble(sampledArray[(i + coordX) + arrayWidth * (j + (coordZ + 1))], weight * (1 - deltaX) * deltaZ, Double::sum);
                weightMap.mergeDouble(sampledArray[(i + (coordX + 1)) + arrayWidth * (j + coordZ)], weight * deltaX * (1 - deltaZ), Double::sum);
                weightMap.mergeDouble(sampledArray[(i + coordX) + arrayWidth * (j + coordZ)], weight * (1 - deltaX) * (1 - deltaZ), Double::sum);
            }
        }
    }

    public static <T, G extends Enum<G>> void reduceGroupedWeightMap(Object2DoubleMap<T> weightMap, Object2DoubleMap<T> groupWeightMap, Function<T, G> groupFunction, int groups)
    {
        // First, we need to calculate the maximum weight per group
        double[] maxWeights = new double[groups];
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            G group = groupFunction.apply(entry.getKey());
            if (group != null)
            {
                maxWeights[group.ordinal()] += entry.getDoubleValue();
            }
        }
        // Then, we iterate through the smaller weight map and identify the actual weight that needs to be replaced with each group
        double[] actualWeights = new double[groups];
        ObjectIterator<Object2DoubleMap.Entry<T>> iterator = weightMap.object2DoubleEntrySet().iterator();
        while (iterator.hasNext())
        {
            Object2DoubleMap.Entry<T> entry = iterator.next();
            G group = groupFunction.apply(entry.getKey());
            if (group != null)
            {
                actualWeights[group.ordinal()] += entry.getDoubleValue();
                iterator.remove();
            }
        }
        // Finally, insert the weights for each group as a portion of the actual weight
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            G group = groupFunction.apply(entry.getKey());
            if (group != null && actualWeights[group.ordinal()] > 0 && maxWeights[group.ordinal()] > 0)
            {
                weightMap.put(entry.getKey(), entry.getDoubleValue() * actualWeights[group.ordinal()] / maxWeights[group.ordinal()]);
            }
        }
    }

    public interface CoordinateAccessor<T>
    {
        T get(int x, int z);
    }
}