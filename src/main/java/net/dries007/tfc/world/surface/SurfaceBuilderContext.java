/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;

public class SurfaceBuilderContext
{
    private final LevelAccessor level;
    private final ChunkAccess chunk;
    private final ChunkData chunkData;
    private final RockData rockData;
    private final RandomSource random;
    private final long seed;
    private final int seaLevel;
    private final int minY;

    private final Set<BlockState> defaultBlockStates;
    private final Set<BlockState> defaultFluidStates;

    private final BlockPos.MutableBlockPos cursor;

    @Nullable private BiomeExtension biome;
    @Nullable private BiomeExtension originalBiome;
    private double biomeWeight;
    private double slope;
    private float temperature;
    private float groundwater;
    private boolean salty;

    public SurfaceBuilderContext(LevelAccessor level, ChunkAccess chunk, ChunkData chunkData, RandomSource random, long seed, RockLayerSettings rockLayerSettings, int seaLevel, int minY)
    {
        this.level = level;
        this.chunk = chunk;
        this.chunkData = chunkData;
        this.rockData = chunkData.getRockData();
        this.random = random;
        this.seed = seed;
        this.seaLevel = seaLevel;
        this.minY = minY;

        this.defaultBlockStates = new ObjectOpenHashSet<>();
        this.defaultFluidStates = new ObjectOpenHashSet<>();

        this.cursor = new BlockPos.MutableBlockPos();

        for (RockSettings rock : rockLayerSettings.getRocks())
        {
            defaultBlockStates.add(rock.raw().defaultBlockState());
        }
        defaultFluidStates.add(Blocks.WATER.defaultBlockState());
    }

    public void buildSurface(BiomeExtension biome, BiomeExtension originalBiome, double biomeWeight, boolean salty, SurfaceBuilder builder, int x, int y, int z, double slope)
    {
        this.biome = biome;
        this.originalBiome = originalBiome;
        this.biomeWeight = biomeWeight;
        this.slope = slope;
        this.temperature = chunkData.getAverageTemp(x, z);
        this.groundwater = chunkData.getGroundwater(x, z);
        this.salty = salty;

        // We iterate down based on the actual surface height (since our capability for overhangs is much more limited than vanilla)
        final int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        final int actualMinSurfaceHeight = Math.max(minY, Math.min(y, oceanFloor) - 20); // Iterate down to at least the ocean floor and below

        cursor.set(x, 0, z);
        builder.buildSurface(this, y, actualMinSurfaceHeight);
    }

    public BiomeExtension biome()
    {
        assert biome != null;
        return biome;
    }

    /**
     * Will never be a river
     */
    public BiomeExtension originalBiome()
    {
        assert originalBiome != null;
        return originalBiome;
    }

    public double weight()
    {
        return biomeWeight;
    }

    public BlockPos pos()
    {
        return cursor;
    }

    public RockSettings getRock()
    {
        return rockData.getRock(cursor.getX(), cursor.getY(), cursor.getZ());
    }

    public RockSettings getSeaLevelRock()
    {
        return rockData.getRock(cursor.getX(), seaLevel, cursor.getZ());
    }

    public RockSettings getBottomRock()
    {
        return rockData.getRock(cursor.getX(), -64, cursor.getZ());
    }

    public float averageTemperature()
    {
        return temperature;
    }

    public float groundwater()
    {
        return groundwater;
    }

    public boolean salty()
    {
        return salty;
    }

    public BlockState getBlockState(int y)
    {
        return chunk.getBlockState(cursor.setY(y));
    }

    public void setBlockState(int y, SurfaceState state)
    {
        cursor.setY(y);
        state.setState(this);
    }

    public void setBlockState(int y, BlockState state)
    {
        chunk.setBlockState(cursor.setY(y), state, false);
    }

    public LevelAccessor level()
    {
        return level;
    }

    public ChunkAccess chunk()
    {
        return chunk;
    }

    public ChunkData getChunkData()
    {
        return chunkData;
    }

    public RandomSource random()
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

    public double getSlope()
    {
        return slope;
    }

    public void setSlope(double slope)
    {
        this.slope = slope;
    }

    public boolean isDefaultBlock(BlockState state)
    {
        return defaultBlockStates.contains(state);
    }

    public boolean isDefaultFluid(BlockState state)
    {
        return defaultFluidStates.contains(state);
    }


    /**
     * Calculates a surface depth value, taking into account altitude and slope
     *
     * @param y                  The y value. Values over sea level (96) are treated as lower depth
     * @param maxDepth           The maximum surface depth
     * @param minimumReturnValue The minimum possible slope. Typically 0, -1 is used as a flag value for not placing the top surface layer on occasion.
     * @return a surface depth in the range [minimumReturnValue, maxSlope]
     */
    public int calculateAltitudeSlopeSurfaceDepth(int y, int maxDepth, int minimumReturnValue)
    {
        final double slopeFactor = 1 - Mth.clamp(slope / 15d, 0, 1); // Large = low slope
        final double altitudeFactor = y < seaLevel ?
            Mth.clampedMap((seaLevel - y) / 15d, 0, 0.4, 1, 1.4) : // Altitudes below sea level have larger depth
            Mth.clampedMap((y - seaLevel) / 140d, 0, 0.8, 1, 0.2); // Altitudes above sea level have slightly lower depth

        return Mth.clamp((int) Mth.lerp(slopeFactor * altitudeFactor, minimumReturnValue, maxDepth), minimumReturnValue, maxDepth);
    }
}
