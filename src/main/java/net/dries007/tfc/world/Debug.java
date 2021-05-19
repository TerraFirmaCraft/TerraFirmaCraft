/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;

/**
 * A collection of debug world generation things
 */
@SuppressWarnings("unused")
public final class Debug
{
    /* Toggle to only generate biomes with normal/normal climates. This can assist when debugging specific biomes, as /locatebiome works much more readily. */
    public static final boolean ONLY_NORMAL_NORMAL_CLIMATES = false;

    /* Cover the world in a visualization of the slope, which is used to seed surface depth. */
    public static final boolean ENABLE_SLOPE_VISUALIZATION = false;

    /* Only generate a single biome in the world */
    public static final boolean SINGLE_BIOME = false;
    public static final BiomeVariants SINGLE_BIOME_BIOME = TFCBiomes.OCEAN;

    @FunctionalInterface
    interface SlopeFunction
    {
        double sampleSlope(double[] slopeMap, int x, int z);
    }

    public static void slopeVisualization(IChunk chunk, double[] slopeMap, int chunkX, int chunkZ, SlopeFunction slopeFunction)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final Block[] meter = new Block[] {
            Blocks.WHITE_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS,
            Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.MAGENTA_STAINED_GLASS,
            Blocks.PINK_STAINED_GLASS
        };

        final Heightmap heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                int y = chunk.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
                mutablePos.set(chunkX + x, y, chunkZ + z);
                double slope = slopeFunction.sampleSlope(slopeMap, x, z);
                int slopeIndex = MathHelper.clamp((int) slope, 0, meter.length - 1);
                chunk.setBlockState(mutablePos, meter[slopeIndex].defaultBlockState(), false);
            }
        }
    }
}
