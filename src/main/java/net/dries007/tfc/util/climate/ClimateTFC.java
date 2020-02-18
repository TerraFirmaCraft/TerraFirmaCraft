/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

public final class ClimateTFC
{
    private static final ClimateCache CACHE = new ClimateCache();

    /*
    public static float getActualTemp(World world, BlockPos pos)
    {
        return getActualTemp(world, pos, 0);
    }

    public static float getActualTemp(World world, BlockPos pos, long timeOffset)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(cap -> ClimateHelper.actualTemp(cap.getRegionalTemp(), pos.getY(), pos.getZ(), timeOffset));
        }
        return getActualTemp(pos, timeOffset);
    }

    public static float getActualTemp(BlockPos pos)
    {
        return getActualTemp(pos, 0);
    }

    public static float getActualTemp(BlockPos pos, long timeOffset)
    {
        return ClimateHelper.actualTemp(CACHE.get(pos).getRegionalTemp(), pos.getY(), pos.getZ(), timeOffset);
    }

    public static float getDailyTemp(World world, BlockPos pos)
    {
        return getDailyTemp(world, pos, 0);
    }

    public static float getDailyTemp(World world, BlockPos pos, long timeOffset)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(cap -> ClimateHelper.dailyTemp(cap.getRegionalTemp(), pos.getY(), pos.getZ(), timeOffset));
        }
        return getDailyTemp(pos, timeOffset);
    }

    public static float getDailyTemp(BlockPos pos)
    {
        return getDailyTemp(pos, 0);
    }

    public static float getDailyTemp(BlockPos pos, long timeOffset)
    {
        return ClimateHelper.dailyTemp(CACHE.get(pos).getRegionalTemp(), pos.getZ(), timeOffset);
    }


    public static float getMonthlyTemp(World world, BlockPos pos)
    {
        return getMonthlyTemp(world, pos, 0);
    }

    public static float getMonthlyTemp(World world, BlockPos pos, long timeOffset)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(cap -> ClimateHelper.monthlyTemp(cap.getRegionalTemp(), pos.getZ(), timeOffset));
        }
        return getMonthlyTemp(pos, timeOffset);
    }

    public static float getMonthlyTemp(BlockPos pos)
    {
        return getMonthlyTemp(pos, 0);
    }

    public static float getMonthlyTemp(BlockPos pos, long timeOffset)
    {
        return ClimateHelper.monthlyTemp(CACHE.get(pos).getRegionalTemp(), pos.getZ(), timeOffset);
    }
    */

    public static float getAvgTemp(World world, BlockPos pos)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(ChunkData::getAvgTemp).orElseGet(() -> getAvgTemp(pos));
        }
        return getAvgTemp(pos);
    }

    public static float getAvgTemp(BlockPos pos)
    {
        return ClimateHelper.monthFactor(CACHE.get(pos).getRegionalTemp(), Month.AVERAGE_TEMPERATURE_MODIFIER, pos.getZ());
    }

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
        return CACHE.get(pos).getRainfall();
    }

    public static void update(ChunkPos pos, float temperature, float rainfall)
    {
        CACHE.update(pos, temperature, rainfall);
    }

    private ClimateTFC() {}
}
