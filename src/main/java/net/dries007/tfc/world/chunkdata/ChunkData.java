/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.util.LerpFloatLayer;

public class ChunkData implements ICapabilitySerializable<CompoundNBT>
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    private static final Logger LOGGER = LogManager.getLogger();

    public static ChunkData get(IWorld world, BlockPos pos)
    {
        return get(world, new ChunkPos(pos));
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request packets if necessary
     * If on server, will either query capability falling back to cache, or query provider to generate the data.
     *
     * @see ChunkDataProvider#get(ChunkPos, Status) to directly force chunk generation, or if a world is not available
     * @see ChunkDataCache#get(ChunkPos) to directly access the cache
     */
    public static ChunkData get(IWorld world, ChunkPos pos)
    {
        // Query cache first, picking the correct cache for the current logical side
        ChunkData data = ChunkDataCache.get(world).get(pos);
        if (data == null)
        {
            return getCapability(world.hasChunk(pos.x, pos.z) ? world.getChunk(pos.getWorldPosition()) : null).orElse(ChunkData.EMPTY);
        }
        return data;
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static LazyOptional<ChunkData> getCapability(@Nullable IChunk chunk)
    {
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY);
        }
        return LazyOptional.empty();
    }

    private final LazyOptional<ChunkData> capability;
    private final ChunkPos pos;

    private RockData rockData;
    private Status status;
    private LerpFloatLayer rainfallLayer;
    private LerpFloatLayer temperatureLayer;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;

    public ChunkData(ChunkPos pos)
    {
        this.pos = pos;
        this.capability = LazyOptional.of(() -> this);

        reset();
    }

    public RockData getRockData()
    {
        return rockData;
    }

    public void setRockData(RockData rockData)
    {
        this.rockData = rockData;
    }

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX() & 15, pos.getZ() & 15);
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer.getValue(-z / 16f, x / 16f);
    }

    public void setRainfall(float rainNW, float rainNE, float rainSW, float rainSE)
    {
        rainfallLayer.init(rainNW, rainNE, rainSW, rainSE);
    }

    public float getAverageTemp(BlockPos pos)
    {
        return getAverageTemp(pos.getX() & 15, pos.getZ() & 15);
    }

    public float getAverageTemp(int x, int z)
    {
        return temperatureLayer.getValue(-z / 16f, x / 16f);
    }

    public void setAverageTemp(float tempNW, float tempNE, float tempSW, float tempSE)
    {
        temperatureLayer.init(tempNW, tempNE, tempSW, tempSE);
    }

    public void setFloraData(ForestType forestType, float forestWeirdness, float forestDensity)
    {
        this.forestType = forestType;
        this.forestWeirdness = forestWeirdness;
        this.forestDensity = forestDensity;
    }

    public ForestType getForestType()
    {
        return forestType;
    }

    public float getForestWeirdness()
    {
        return forestWeirdness;
    }

    public float getForestDensity()
    {
        return forestDensity;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * @return If the current chunk data is empty, then return other
     */
    public ChunkData ifEmptyGet(Supplier<ChunkData> other)
    {
        return status != Status.EMPTY ? this : other.get();
    }

    /**
     * Create an update packet to send to client with necessary information
     */
    public ChunkWatchPacket getUpdatePacket()
    {
        return new ChunkWatchPacket(pos.x, pos.z, rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness);
    }

    /**
     * Called on client, sets to received data
     */
    public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;

        if (status == Status.CLIENT || status == Status.EMPTY)
        {
            this.status = Status.CLIENT;
        }
        else
        {
            LOGGER.warn("Called client side update method on server side chunk data: {}. This should not happen.", this);
        }
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

        nbt.putByte("status", (byte) status.ordinal());
        if (status.isAtLeast(Status.CLIMATE))
        {
            nbt.put("rainfall", rainfallLayer.serializeNBT());
            nbt.put("temperature", temperatureLayer.serializeNBT());
        }
        if (status.isAtLeast(Status.ROCKS))
        {
            nbt.put("rockData", rockData.serializeNBT());
        }
        if (status.isAtLeast(Status.FLORA))
        {
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            status = Status.valueOf(nbt.getByte("status"));
            if (status.isAtLeast(Status.CLIMATE))
            {
                rainfallLayer.deserializeNBT(nbt.getCompound("rainfall"));
                temperatureLayer.deserializeNBT(nbt.getCompound("temperature"));
            }
            if (status.isAtLeast(Status.ROCKS))
            {
                rockData.deserializeNBT(nbt.getCompound("rockData"));
            }
            if (status.isAtLeast(Status.FLORA))
            {
                forestType = ForestType.valueOf(nbt.getByte("forestType"));
                forestWeirdness = nbt.getFloat("forestWeirdness");
                forestDensity = nbt.getFloat("forestDensity");
            }
        }
    }

    @Override
    public String toString()
    {
        return "ChunkData{pos=" + pos + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    private void reset()
    {
        rockData = new RockData();
        rainfallLayer = new LerpFloatLayer(250);
        temperatureLayer = new LerpFloatLayer(10);
        forestWeirdness = 0.5f;
        forestDensity = 0.5f;
        forestType = ForestType.NONE;
        status = Status.EMPTY;
    }

    public enum Status
    {
        EMPTY,
        CLIENT,
        CLIMATE,
        ROCKS,
        FLORA;

        private static final Status[] VALUES = values();

        public static Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : EMPTY;
        }

        public boolean isAtLeast(Status otherStatus)
        {
            return this.ordinal() >= otherStatus.ordinal();
        }

        public boolean isAtMost(Status otherStatus)
        {
            return this.ordinal() <= otherStatus.ordinal();
        }
    }

    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is specifically for an immutable empty copy, representing invalid chunk data
     */
    private static class Immutable extends ChunkData
    {
        private Immutable()
        {
            super(new ChunkPos(ChunkPos.INVALID_CHUNK_POS));
        }

        @Override
        public void setRockData(RockData rockData)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setRainfall(float rainNW, float rainNE, float rainSW, float rainSE)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setAverageTemp(float tempNW, float tempNE, float tempSW, float tempSE)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setFloraData(ForestType forestType, float forestWeirdness, float forestDensity)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setStatus(Status status)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public String toString()
        {
            return "ImmutableChunkData";
        }
    }
}