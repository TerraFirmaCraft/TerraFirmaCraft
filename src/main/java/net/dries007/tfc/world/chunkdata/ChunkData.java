/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ChunkData implements ICapabilitySerializable<CompoundNBT>
{
    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static LazyOptional<ChunkData> get(IChunk chunk)
    {
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY);
        }
        return LazyOptional.empty();
    }

    private final LazyOptional<ChunkData> capability = LazyOptional.of(() -> this);

    private RockData rockData;
    private float rainfall;
    private float averageTemp;
    private Status status;

    public ChunkData()
    {
        initWithDefaultValues();
    }

    public RockData getRockData()
    {
        return rockData;
    }

    public void setRockData(RockData rockData)
    {
        this.rockData = rockData;
    }

    public float getRainfall()
    {
        return rainfall;
    }

    public void setRainfall(float rainfall)
    {
        this.rainfall = rainfall;
    }

    public float getAverageTemp()
    {
        return averageTemp;
    }

    public void setAverageTemp(float regionalTemp)
    {
        this.averageTemp = regionalTemp;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ChunkDataCapability.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putInt("status", status.ordinal());
        if (status.isAtLeast(Status.CLIMATE))
        {
            nbt.putFloat("rainfall", rainfall);
            nbt.putFloat("averageTemp", averageTemp);
        }
        if (status.isAtLeast(Status.ROCKS))
        {
            nbt.put("rockData", rockData.serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            status = Status.valueOf(nbt.getInt("status"));
            initWithDefaultValues();
            if (status.isAtLeast(Status.CLIMATE))
            {
                rainfall = nbt.getFloat("rainfall");
                averageTemp = nbt.getFloat("averageTemp");
            }
            if (status.isAtLeast(Status.ROCKS))
            {
                rockData.deserializeNBT(nbt.getCompound("rockData"));
            }
        }
    }

    private void initWithDefaultValues()
    {
        rockData = new RockData();
        rainfall = 250;
        averageTemp = 10;
        status = Status.DEFAULT;
    }

    public enum Status
    {
        DEFAULT,
        CLIMATE,
        ROCKS,
        FULL;

        private static final Status[] VALUES = values();

        public static Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : DEFAULT;
        }

        public boolean isAtLeast(Status otherStatus)
        {
            return this.ordinal() >= otherStatus.ordinal();
        }
    }
}
