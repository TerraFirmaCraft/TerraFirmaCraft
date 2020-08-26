/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class MountainSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    public MountainSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        double heightNoise = noise * 3f + startHeight;
        if (heightNoise > 130)
        {
            TFCSurfaceBuilders.NORMAL.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG);
        }
        else
        {
            TFCSurfaceBuilders.THIN.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
        }
    }
}
