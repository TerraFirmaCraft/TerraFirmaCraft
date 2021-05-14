/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class SurfaceBuilderContext
{
    private final IWorld world;
    private final ChunkPrimer chunk;
    private final Heightmap worldSurfaceHeightmap, oceanFloorHeightmap;
    private final ChunkData chunkData;
    private final Random random;
    private final long seed;
    private final DimensionSettings settings;
    private final int seaLevel;
    private final int chunkX, chunkZ;

    public SurfaceBuilderContext(IWorld world, ChunkPrimer chunk, ChunkData chunkData, Random random, long seed, DimensionSettings settings, int seaLevel)
    {
        this.world = world;
        this.chunk = chunk;
        this.worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);
        this.oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        this.chunkData = chunkData;
        this.random = random;
        this.seed = seed;
        this.settings = settings;
        this.seaLevel = seaLevel;
        this.chunkX = chunk.getPos().getMinBlockX();
        this.chunkZ = chunk.getPos().getMinBlockZ();
    }

    public <C extends ISurfaceBuilderConfig> void apply(ConfiguredSurfaceBuilder<C> surfaceBuilder, Biome biome, int x, int z, int startHeight, double noise, double slope)
    {
        apply(surfaceBuilder.surfaceBuilder, biome, x, z, startHeight, noise, slope, surfaceBuilder.config);
    }

    public <C extends ISurfaceBuilderConfig> void apply(SurfaceBuilder<C> surfaceBuilder, Biome biome, int x, int z, int startHeight, double noise, double slope, C config)
    {
        surfaceBuilder.initNoise(seed);
        if (surfaceBuilder instanceof ContextSurfaceBuilder)
        {
            final float temperature = chunkData.getAverageTemp(x, z);
            final float rainfall = chunkData.getRainfall(x, z);
            final boolean saltWater = TFCBiomes.getExtensionOrThrow(world, biome).getVariants().isSalty();

            ((ContextSurfaceBuilder<C>) surfaceBuilder).apply(this, biome, x, z, startHeight, noise, slope, temperature, rainfall, saltWater, config);
        }
        else
        {
            // Apply vanilla and hope for the best
            surfaceBuilder.apply(random, chunk, biome, x, z, startHeight, noise, settings.getDefaultBlock(), settings.getDefaultFluid(), seaLevel, seed, config);
        }
    }

    public BlockState getBlockState(BlockPos pos)
    {
        return chunk.getBlockState(pos);
    }

    public void setBlockState(BlockPos pos, ISurfaceState state, float temperature, float rainfall, boolean salty)
    {
        state.place(this, pos, chunkX | pos.getX(), chunkZ | pos.getZ(), chunkData.getRockData(), temperature, rainfall, salty);
    }

    public void setBlockState(BlockPos pos, BlockState state)
    {
        // Skip unnecessary steps in ChunkPrimer#setBlockState
        final int x = pos.getX() & 15, y = pos.getY(), z = pos.getZ() & 15;
        if (!World.isOutsideBuildHeight(y))
        {
            if (chunk.getSections()[y >> 4] != Chunk.EMPTY_SECTION || !state.is(Blocks.AIR))
            {
                final ChunkSection section = chunk.getOrCreateSection(y >> 4);
                section.setBlockState(x, y & 15, z, state, false);
                worldSurfaceHeightmap.update(x, y, z, state);
                oceanFloorHeightmap.update(x, y, z, state);
            }
        }
    }

    public IWorld getWorld()
    {
        return world;
    }

    public IChunk getChunk()
    {
        return chunk;
    }

    public ChunkData getChunkData()
    {
        return chunkData;
    }

    public Random getRandom()
    {
        return random;
    }

    public long getSeed()
    {
        return seed;
    }

    public int getSeaLevel()
    {
        return seaLevel;
    }

    public BlockState getDefaultBlock()
    {
        return settings.getDefaultBlock();
    }

    public BlockState getDefaultFluid()
    {
        return settings.getDefaultFluid();
    }
}
