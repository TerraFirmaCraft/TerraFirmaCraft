/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.network.ChunkWatchPacket;

public class ChunkData implements ICapabilitySerializable<CompoundNBT>
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    public static ChunkData get(IWorld world, BlockPos pos)
    {
        return get(world, new ChunkPos(pos));
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request packets if necessary
     * If on server, will either query capability falling back to cache, or query provider to generate the data.
     *
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

    private Status status;

    private RockData rockData;
    private LerpFloatLayer rainfallLayer;
    private LerpFloatLayer temperatureLayer;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;
    private PlateTectonicsClassification plateTectonicsInfo;

    public ChunkData(ChunkPos pos)
    {
        this.pos = pos;
        this.capability = LazyOptional.of(() -> this);

        reset();
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    /**
     * Can be null when the chunk data's underlying status is EMPTY or CLIENT
     */
    @Nullable
    public RockData getRockData()
    {
        return rockData;
    }

    public RockData getRockDataOrThrow()
    {
        return Objects.requireNonNull(rockData, () -> "Expected rock data to be present on " + this);
    }

    public void setRockData(RockData rockData)
    {
        this.rockData = rockData;
    }

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX(), pos.getZ());
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer.getValue((z & 15) / 16f, 1 - ((x & 15) / 16f));
    }

    public void setRainfall(float rainNW, float rainNE, float rainSW, float rainSE)
    {
        rainfallLayer.init(rainNW, rainNE, rainSW, rainSE);
    }

    public float getAverageTemp(BlockPos pos)
    {
        return getAverageTemp(pos.getX(), pos.getZ());
    }

    public float getAverageTemp(int x, int z)
    {
        return temperatureLayer.getValue((z & 15) / 16f, 1 - ((x & 15) / 16f));
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

    /**
     * @return A value in [0, 1]
     */
    public float getForestDensity()
    {
        return forestDensity;
    }

    /**
     * Returns a standard calculated value for density.
     * This scales the regular density by the forest type.
     *
     * @return a value in [0, 1]
     */
    public float getAdjustedForestDensity()
    {
        return forestDensity * 0.6f + 0.4f * forestType.ordinal() / 4f;
    }

    public PlateTectonicsClassification getPlateTectonicsInfo()
    {
        return plateTectonicsInfo;
    }

    public void setPlateTectonicsInfo(PlateTectonicsClassification plateTectonicsInfo)
    {
        this.plateTectonicsInfo = plateTectonicsInfo;
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
     * Create an update packet to send to client with necessary information
     */
    public ChunkWatchPacket getUpdatePacket()
    {
        return new ChunkWatchPacket(pos.x, pos.z, rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness, plateTectonicsInfo);
    }

    /**
     * Called on client, sets to received data
     */
    public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
        this.plateTectonicsInfo = plateTectonicsInfo;

        if (status == Status.EMPTY)
        {
            this.status = Status.CLIENT;
        }
        else
        {
            throw new IllegalStateException("ChunkData#onUpdatePacket was called on non client side chunk data: " + this);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ChunkDataCapability.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("status", (byte) status.ordinal());
        if (status == Status.FULL)
        {
            nbt.putByte("plateTectonicsInfo", (byte) plateTectonicsInfo.ordinal());
            nbt.put("rainfall", rainfallLayer.serializeNBT());
            nbt.put("temperature", temperatureLayer.serializeNBT());
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
            if (rockData != null)
            {
                nbt.put("rockData", rockData.serializeNBT());
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        reset();
        status = Status.valueOf(nbt.getByte("status"));
        if (status == Status.FULL)
        {
            plateTectonicsInfo = PlateTectonicsClassification.valueOf(nbt.getByte("plateTectonicsInfo"));
            rainfallLayer.deserializeNBT(nbt.getCompound("rainfall"));
            temperatureLayer.deserializeNBT(nbt.getCompound("temperature"));
            rockData = nbt.contains("rockData", Constants.NBT.TAG_COMPOUND) ? new RockData(nbt.getCompound("rockData")) : null;
            forestType = ForestType.valueOf(nbt.getByte("forestType"));
            forestWeirdness = nbt.getFloat("forestWeirdness");
            forestDensity = nbt.getFloat("forestDensity");
        }
    }

    @Override
    public String toString()
    {
        return "ChunkData{pos=" + pos + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    private void reset()
    {
        rockData = null;
        rainfallLayer = new LerpFloatLayer(250);
        temperatureLayer = new LerpFloatLayer(10);
        forestWeirdness = 0.5f;
        forestDensity = 0.5f;
        forestType = ForestType.NONE;
        status = Status.EMPTY;
        plateTectonicsInfo = PlateTectonicsClassification.OCEANIC;
    }

    public enum Status
    {
        EMPTY, // Default, un-generated chunk data
        CLIENT, // Client-side shallow copy
        FULL; // Fully generated chunk data

        private static final Status[] VALUES = values();

        public static Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : EMPTY;
        }
    }

    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is specifically for an immutable empty copy, representing invalid chunk data
     */
    private static final class Immutable extends ChunkData
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
        public void setPlateTectonicsInfo(PlateTectonicsClassification plateTectonicsInfo)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setStatus(Status status)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
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