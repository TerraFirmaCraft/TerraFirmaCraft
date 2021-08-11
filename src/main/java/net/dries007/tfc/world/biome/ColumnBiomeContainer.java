/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.IdMap;
import net.minecraft.Util;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * An optimized {@link ChunkBiomeContainer} for world generation where biomes are not actually 3D
 * Avoids the expensive call to {@link BiomeSource#getNoiseBiome(int, int, int)}
 */
public class ColumnBiomeContainer extends ChunkBiomeContainer
{
    // Copied from ChunkBiomeContainer
    public static final int WIDTH_BITS = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2; // 2
    public static final int HORIZONTAL_BITS = WIDTH_BITS + WIDTH_BITS; // 4

    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int MAX_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + DimensionType.BITS_FOR_Y - 2;

    public ColumnBiomeContainer(IdMap<Biome> biomeIdRegistry, LevelHeightAccessor level, ChunkPos chunkPos, BiomeSource biomeSource)
    {
        // Use Util.make to pass the already initialized biomes array into the correct constructor
        // This copies the initialization except it only queries the biome provider once per column, saving 98% of the biome generation calls
        super(biomeIdRegistry, level, Util.make(() -> {
            Biome[] biomes = new Biome[MAX_SIZE];
            int biomeCoordX = chunkPos.getMinBlockX() >> 2;
            int biomeCoordZ = chunkPos.getMinBlockZ() >> 2;
            int quartMinY = QuartPos.fromBlock(level.getMinBuildHeight());
            int quartHeight = QuartPos.fromBlock(level.getHeight()) - 1;

            for (int index = 0; index < (1 << HORIZONTAL_BITS); ++index)
            {
                int x = index & HORIZONTAL_MASK;
                int z = (index >> WIDTH_BITS) & HORIZONTAL_MASK;
                Biome columnBiome = biomeSource.getNoiseBiome(biomeCoordX + x, 0, biomeCoordZ + z);
                for (int y = 0; y <= quartHeight; y++)
                {
                    biomes[index | (QuartPos.toBlock(y) - quartMinY)] = columnBiome;
                }
            }
            return biomes;
        }));
    }
}