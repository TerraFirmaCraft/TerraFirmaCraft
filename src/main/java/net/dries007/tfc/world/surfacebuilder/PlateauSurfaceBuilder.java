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

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class PlateauSurfaceBuilder extends SeedSurfaceBuilder<SurfaceBuilderConfig>
{
    private INoise2D surfaceMaterialNoise;

    public PlateauSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        double heightNoise = noise * 3f + startHeight;
        if (heightNoise > 130)
        {
            float surfaceMaterialValue = surfaceMaterialNoise.noise(x, z) + 0.1f * random.nextFloat() - 0.05f;
            if (surfaceMaterialValue > 0.3f)
            {
                TFCSurfaceBuilders.NORMAL.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.COBBLE_COBBLE_RED_SAND_CONFIG);
            }
            else if (surfaceMaterialValue < -0.3f)
            {
                TFCSurfaceBuilders.NORMAL.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRAVEL_CONFIG);
            }
            else
            {
                TFCSurfaceBuilders.NORMAL.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG);
            }
        }
        else
        {
            TFCSurfaceBuilders.THIN.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        surfaceMaterialNoise = new SimplexNoise2D(seed).octaves(2).spread(0.02f);
    }
}
