/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;

public class SurfaceBuilderContext
{
    private final LevelAccessor world;
    private final ProtoChunk chunk;
    private final Heightmap worldSurfaceHeightmap, oceanFloorHeightmap;
    private final ChunkData chunkData;
    private final RockData rockData;
    private final Random random;
    private final long seed;
    private final NoiseGeneratorSettings settings;
    private final int seaLevel;
    private final int chunkX, chunkZ;

    public SurfaceBuilderContext(LevelAccessor world, ProtoChunk chunk, ChunkData chunkData, Random random, long seed, NoiseGeneratorSettings settings, int seaLevel)
    {
        this.world = world;
        this.chunk = chunk;
        this.worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        this.oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        this.chunkData = chunkData;
        this.rockData = chunkData.getRockDataOrThrow();
        this.random = random;
        this.seed = seed;
        this.settings = settings;
        this.seaLevel = seaLevel;
        this.chunkX = chunk.getPos().getMinBlockX();
        this.chunkZ = chunk.getPos().getMinBlockZ();
    }

    public <C extends SurfaceBuilderConfiguration> void apply(ConfiguredSurfaceBuilder<C> surfaceBuilder, Biome biome, int x, int z, int startHeight, double noise, double slope)
    {
        apply(surfaceBuilder.surfaceBuilder, biome, x, z, startHeight, noise, slope, surfaceBuilder.config);
    }

    public <C extends SurfaceBuilderConfiguration> void apply(SurfaceBuilder<C> surfaceBuilder, Biome biome, int x, int z, int startHeight, double noise, double slope, C config)
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
        state.place(this, pos, chunkX | pos.getX(), chunkZ | pos.getZ(), rockData, temperature, rainfall, salty);
    }

    public void setBlockState(BlockPos pos, BlockState state)
    {
        // Skip unnecessary steps in ChunkPrimer#setBlockState
        final int x = pos.getX() & 15, y = pos.getY(), z = pos.getZ() & 15;
        if (!Level.isOutsideBuildHeight(y))
        {
            if (chunk.getSections()[y >> 4] != LevelChunk.EMPTY_SECTION || !state.is(Blocks.AIR))
            {
                final LevelChunkSection section = chunk.getOrCreateSection(y >> 4);
                section.setBlockState(x, y & 15, z, state, false);
                worldSurfaceHeightmap.update(x, y, z, state);
                oceanFloorHeightmap.update(x, y, z, state);
            }
        }
    }

    public LevelAccessor getWorld()
    {
        return world;
    }

    public ChunkAccess getChunk()
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
