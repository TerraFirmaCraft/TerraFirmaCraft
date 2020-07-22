/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

import static net.dries007.tfc.world.TFCOverworldChunkGenerator.PARABOLIC_9x9;

public enum BiomeEdgeType
{
    SINGLE((biomeWeights, biomeProvider, biomeAt, seed, x, z) -> biomeWeights.put(biomeAt, 1)), // No sampling
    SAMPLE_4(applyToWeightedSquare(1)), // 4 block radius
    SAMPLE_8(applyToWeightedSquare(2)), // 8 block radius
    SAMPLE_16(applyToWeightedSquare(4)); // 16 block radius

    private static TFCBiome getBiome(TFCBiomeProvider biomeProvider, long seed, int x, int z)
    {
        //return (TFCBiome) biomeProvider.getAccurateBiome(x, z);
        return (TFCBiome) SmoothColumnBiomeMagnifier.VANILLA.getBiome(seed, x, 0, z, biomeProvider);
    }

    private static Function applyToWeightedSquare(int size)
    {
        return (biomeWeights, biomeProvider, biomeAt, seed, x, z) -> {
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                {
                    TFCBiome biome = getBiome(biomeProvider, seed, x + (i - 4) * size, z + (j - 4) * size);
                    biomeWeights.mergeDouble(biome, PARABOLIC_9x9[i + 9 * j], Double::sum);
                }
            }
        };
    }

    private final Function function;

    BiomeEdgeType(Function function)
    {
        this.function = function;
    }

    public void apply(Object2DoubleMap<TFCBiome> biomeWeights, TFCBiomeProvider biomeProvider, TFCBiome biomeAt, long seed, int x, int z)
    {
        function.apply(biomeWeights, biomeProvider, biomeAt, seed, x, z);
    }

    interface Function
    {
        void apply(Object2DoubleMap<TFCBiome> biomeWeights, TFCBiomeProvider biomeProvider, TFCBiome biomeAt, long seed, int x, int z);
    }
}
