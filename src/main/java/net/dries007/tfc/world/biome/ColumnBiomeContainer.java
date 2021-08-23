/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.IdMap;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;

/**
 * An optimized {@link ChunkBiomeContainer} for world generation where biomes are not actually 3D
 * Avoids the expensive call to {@link BiomeSource#getNoiseBiome(int, int, int)}
 */
public class ColumnBiomeContainer extends ChunkBiomeContainer
{
    // Copied from ChunkBiomeContainer
    public static final int WIDTH_BITS = Mth.ceillog2(16) - 2; // 2
    public static final int HORIZONTAL_BITS = WIDTH_BITS + WIDTH_BITS; // 4
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1; // 0b11

    private static Biome[] sampleBiomes(LevelHeightAccessor level, ChunkPos chunkPos, BiomeSource source)
    {
        final int quartX = QuartPos.fromBlock(chunkPos.getMinBlockX());
        final int quartY = QuartPos.fromBlock(level.getMinBuildHeight());
        final int quartZ = QuartPos.fromBlock(chunkPos.getMinBlockZ());

        final int height = (level.getHeight() + 4 - 1) / 4;
        final Biome[] biomes = new Biome[(1 << HORIZONTAL_BITS) * height];

        for (int i = 0; i < (1 << HORIZONTAL_BITS); ++i)
        {
            final int x = quartX + (i & HORIZONTAL_MASK);
            final int z = quartZ + ((i >> WIDTH_BITS) & HORIZONTAL_MASK);
            final Biome biome = source.getNoiseBiome(x, quartY, z);
            for (int dy = 0; dy < height; dy++)
            {
                biomes[i | (dy << HORIZONTAL_BITS)] = biome;
            }
        }

        return biomes;
    }

    public ColumnBiomeContainer(IdMap<Biome> biomeIdRegistry, LevelHeightAccessor level, ChunkPos chunkPos, BiomeSource source)
    {
        super(biomeIdRegistry, level, sampleBiomes(level, chunkPos, source));
    }
}