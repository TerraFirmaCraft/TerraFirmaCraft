/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomSource;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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

    @Nullable private Biome biome;
    private double biomeWeight;
    private double slope;
    private float temperature;
    private float rainfall;
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

    public void buildSurface(Biome biome, double biomeWeight, boolean salty, SurfaceBuilder builder, int x, int y, int z, double slope)
    {
        this.biome = biome;
        this.biomeWeight = biomeWeight;
        this.slope = slope;
        this.temperature = chunkData.getAverageTemp(x, z);
        this.rainfall = chunkData.getRainfall(x, z);
        this.salty = salty;

        // We iterate down based on the actual surface height (since our capability for overhangs is much more limited than vanilla)
        final int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        final int actualMinSurfaceHeight = Math.max(minY, Math.min(y, oceanFloor) - 20); // Iterate down to at least the ocean floor and below

        cursor.set(x, 0, z);
        builder.buildSurface(this, y, actualMinSurfaceHeight);
    }

    public Biome biome()
    {
        assert biome != null;
        return biome;
    }

    public double weight()
    {
        return biomeWeight;
    }

    public BlockPos pos()
    {
        return cursor;
    }

    public RockData getRockData()
    {
        return rockData;
    }

    public RockSettings getRock()
    {
        return getRock(cursor.getX(), cursor.getY(), cursor.getZ());
    }

    public RockSettings getBottomRock()
    {
        return rockData.getBottomRock(cursor.getX(), cursor.getZ());
    }

    public RockSettings getRock(int x, int y, int z)
    {
        return rockData.getRock(x, y, z);
    }

    public float averageTemperature()
    {
        return temperature;
    }

    public float rainfall()
    {
        return rainfall;
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

    public boolean isDefaultBlock(BlockState state)
    {
        return defaultBlockStates.contains(state);
    }

    public boolean isDefaultFluid(BlockState state)
    {
        return defaultFluidStates.contains(state);
    }

    public int calculateAltitudeSlopeSurfaceDepth(int y, int maxDepth, double falloff, int minimumReturnValue)
    {
        return calculateAltitudeSlopeSurfaceDepth(y, slope, maxDepth, falloff, minimumReturnValue);
    }

    /**
     * Calculates a surface depth value, taking into account altitude and slope
     *
     * @param y                  The y value. Values over sea level (96) are treated as lower depth
     * @param slope              The slope. Expecting values roughly in the range [0, 13]. Higher values are treated as extreme slopes.
     * @param maxDepth           The maximum surface depth
     * @param falloff            A value between 0 and 1 indicating how quickly the depth decays w.r.t increasing slope or altitude.
     * @param minimumReturnValue The minimum possible slope. Typically 0, -1 is used as a flag value for not placing the top surface layer on occasion.
     * @return a surface depth in the range [minimumReturnValue, maxSlope]
     */
    public int calculateAltitudeSlopeSurfaceDepth(int y, double slope, int maxDepth, double falloff, int minimumReturnValue)
    {
        final double slopeFactor = 1 - Mth.clamp(slope / 15d, 0, 1); // Large = low slope
        final double altitudeFactor = y < seaLevel ?
            Mth.clampedMap((seaLevel - y) / 15d, 0, 0.4, 1, 1.4) : // Altitudes below sea level have have larger depth
            Mth.clampedMap((y - seaLevel) / 140d, 0, 0.8, 1, 0.2); // Altitudes above sea level have slightly lower depth

        return Mth.clamp((int) Mth.lerp(slopeFactor * altitudeFactor, minimumReturnValue, maxDepth), minimumReturnValue, maxDepth);
    }
}
