/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;

/**
 * An optimized {@link BiomeContainer} for world generation where biomes are not actually 3D
 * Avoids the expensive call to {@link BiomeProvider#getNoiseBiome(int, int, int)}
 */
public class ColumnBiomeContainer extends BiomeContainer
{
    // Copied from BiomeContainer
    public static final int WIDTH_BITS = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2; // 2
    public static final int HEIGHT_BITS = (int) Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2; // 6
    public static final int HORIZONTAL_BITS = WIDTH_BITS + WIDTH_BITS; // 4

    public ColumnBiomeContainer(IObjectIntIterable<Biome> biomeIdRegistry, ChunkPos chunkPosIn, BiomeProvider biomeProviderIn)
    {
        // Use Util.make to pass the already initialized biomes array into the correct constructor
        // This copies the initialization except it only queries the biome provider once per column, saving 98% of the biome generation calls
        super(biomeIdRegistry, Util.make(() -> {
            Biome[] biomes = new Biome[BIOMES_SIZE];
            int biomeCoordX = chunkPosIn.getMinBlockX() >> 2;
            int biomeCoordZ = chunkPosIn.getMinBlockZ() >> 2;

            for (int index = 0; index < (1 << HORIZONTAL_BITS); ++index)
            {
                int x = index & HORIZONTAL_MASK;
                int z = (index >> WIDTH_BITS) & HORIZONTAL_MASK;
                Biome columnBiome = biomeProviderIn.getNoiseBiome(biomeCoordX + x, 0, biomeCoordZ + z);
                for (int y = 0; y <= VERTICAL_MASK; y++)
                {
                    biomes[index | (y << HORIZONTAL_BITS)] = columnBiome;
                }
            }
            return biomes;
        }));
    }
}