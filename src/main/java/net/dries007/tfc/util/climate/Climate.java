/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.world.biome.BiomeRainfall;
import net.dries007.tfc.world.biome.BiomeTemperature;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

/**
 * Central class for all TFC climate requirements.
 * This is only valid in the overworld!
 */
public final class Climate
{
    private static final Map<ChunkPos, ClimateData> CACHE = new HashMap<>();

    /**
     * Used for initial biome assignments. TFC overrides this to use out temperature models
     */
    public static Biome.RainType getDefaultRainType(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        if (rainfall == BiomeRainfall.ARID)
        {
            return Biome.RainType.NONE;
        }
        else if (temperature == BiomeTemperature.FROZEN || temperature == BiomeTemperature.COLD)
        {
            return Biome.RainType.SNOW;
        }
        else
        {
            return Biome.RainType.RAIN;
        }
    }

    /**
     * Gets the actual temperature at a location. This includes
     * - the average regional temperature
     * - seasonal / monthly temperature
     * - daily random effects
     * - hourly variation
     * - Y level effects
     */
    public static float getTemp(IWorld world, BlockPos pos, long timeStamp)
    {
        // todo: requires calendar
        return getAvgTemp(world, pos);
    }

    public static float getTemp(BlockPos pos, long timestamp)
    {
        // todo: requires calendar
        return getAvgTemp(pos);
    }


    /**
     * Gets the average temperature of an area
     * Used during world generation
     */
    public static float getAvgTemp(IWorld world, BlockPos pos)
    {
        if (world.chunkExists(pos.getX() << 4, pos.getZ() << 4))
        {
            IChunk chunk = world.getChunk(pos);
            if (chunk instanceof Chunk)
            {
                return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(ChunkData::getAverageTemp).orElseGet(() -> getAvgTemp(pos));
            }
        }
        return getAvgTemp(pos);
    }

    public static float getAvgTemp(BlockPos pos)
    {
        return get(pos).getAverageTemp();
    }

    /**
     * Gets the average rainfall of an area
     * Used during world generation
     */
    public static float getRainfall(World world, BlockPos pos)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(ChunkData::getRainfall).orElseGet(() -> getRainfall(pos));
        }
        return getRainfall(pos);
    }

    public static float getRainfall(BlockPos pos)
    {
        return get(pos).getRainfall();
    }

    public static void update(ChunkPos pos, float temperature, float rainfall)
    {
        CACHE.put(pos, new ClimateData(temperature, rainfall));
    }

    public static ClimateData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    public static ClimateData get(ChunkPos pos)
    {
        return CACHE.getOrDefault(pos, ClimateData.DEFAULT);
    }

    private Climate() {}
}
