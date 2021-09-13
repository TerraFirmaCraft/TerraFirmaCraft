/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public class SurfaceBuilderContext
{
    private final LevelAccessor world;
    private final ProtoChunk chunk;
    private final ChunkData chunkData;
    private final RockData rockData;
    private final Random random;
    private final long seed;
    private final NoiseGeneratorSettings settings;
    private final int seaLevel;
    private final int minY;
    private final int chunkX, chunkZ;

    private final Set<BlockState> defaultBlockStates;
    private final Set<BlockState> defaultFluidStates;

    public SurfaceBuilderContext(LevelAccessor world, ProtoChunk chunk, ChunkData chunkData, Random random, long seed, NoiseGeneratorSettings settings, RockLayerSettings rockLayerSettings, int seaLevel, int minY)
    {
        this.world = world;
        this.chunk = chunk;
        this.chunkData = chunkData;
        this.rockData = chunkData.getRockData();
        this.random = random;
        this.seed = seed;
        this.settings = settings;
        this.seaLevel = seaLevel;
        this.minY = minY;
        this.chunkX = chunk.getPos().getMinBlockX();
        this.chunkZ = chunk.getPos().getMinBlockZ();

        this.defaultBlockStates = new HashSet<>();
        this.defaultFluidStates = new HashSet<>();

        for (RockSettings rock : rockLayerSettings.getRocks())
        {
            defaultBlockStates.add(rock.raw().defaultBlockState());
        }
        defaultFluidStates.add(Blocks.WATER.defaultBlockState());
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

            // We iterate down based on the actual surface height (since our capability for overhangs is much more limited than vanilla)
            final int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x & 15, z & 15);
            final int actualMinSurfaceHeight = Math.max(minY, Math.min(startHeight, oceanFloor) - 20); // Iterate down to at least the ocean floor and below
            ((ContextSurfaceBuilder<C>) surfaceBuilder).apply(this, biome, x, z, startHeight, actualMinSurfaceHeight, noise, slope, temperature, rainfall, saltWater, config);
        }
        else
        {
            // Apply vanilla and hope for the best
            surfaceBuilder.apply(random, chunk, biome, x, z, startHeight, noise, settings.getDefaultBlock(), settings.getDefaultFluid(), seaLevel, settings.getMinSurfaceLevel(), seed, config);
        }
    }

    public BlockState getBlockState(BlockPos pos)
    {
        return chunk.getBlockState(pos);
    }

    public void setBlockState(BlockPos pos, SurfaceState state, float temperature, float rainfall, boolean salty)
    {
        state.place(this, pos, chunkX | pos.getX(), chunkZ | pos.getZ(), rockData, temperature, rainfall, salty);
    }

    public void setBlockState(BlockPos pos, BlockState state)
    {
        chunk.setBlockState(pos, state, false);
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

    public boolean isDefaultBlock(BlockState state)
    {
        return defaultBlockStates.contains(state);
    }

    public boolean isDefaultFluid(BlockState state)
    {
        return defaultFluidStates.contains(state);
    }
}
