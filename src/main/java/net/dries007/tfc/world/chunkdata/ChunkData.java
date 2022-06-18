/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.world.settings.RockLayerSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkData implements ICapabilitySerializable<CompoundTag>
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    private static final float UNKNOWN_RAINFALL = 250;
    private static final float UNKNOWN_TEMPERATURE = 10;

    public static ChunkData get(LevelReader level, BlockPos pos)
    {
        return get(level, new ChunkPos(pos));
    }

    /**
     * Called to get chunk data when a world context is available.
     */
    @SuppressWarnings("deprecation")
    public static ChunkData get(LevelReader level, ChunkPos pos)
    {
        // Query cache first, picking the correct cache for the current logical side
        ChunkData data = ChunkDataCache.get(level).get(pos);
        if (data == null)
        {
            return getCapability(level.hasChunk(pos.x, pos.z) ? level.getChunk(pos.getWorldPosition()) : null).orElse(ChunkData.EMPTY);
        }
        return data;
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static LazyOptional<ChunkData> getCapability(@Nullable ChunkAccess maybe)
    {
        if (maybe instanceof LevelChunk chunk)
        {
            return chunk.getCapability(ChunkDataCapability.CAPABILITY);
        }
        return LazyOptional.empty();
    }

    public static ChunkData createClient(ChunkPos pos)
    {
        return new ChunkData(pos, RockLayerSettings.EMPTY); // Client has empty settings here as it doesn't save
    }

    private final LazyOptional<ChunkData> capability;
    private final RockLayerSettings rockLayerSettings;
    private final ChunkPos pos;

    private Status status;

    @Nullable private RockData rockData;
    @Nullable private LerpFloatLayer rainfallLayer;
    @Nullable private LerpFloatLayer temperatureLayer;
    private int @Nullable [] aquiferSurfaceHeight;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;
    private PlateTectonicsClassification plateTectonicsInfo;

    public ChunkData(ChunkPos pos, RockLayerSettings rockLayerSettings)
    {
        this.pos = pos;
        this.rockLayerSettings = rockLayerSettings;
        this.capability = LazyOptional.of(() -> this);
        this.status = Status.EMPTY;
        this.forestType = ForestType.NONE;
        this.plateTectonicsInfo = PlateTectonicsClassification.OCEANIC;
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    /**
     * Note: this method will throw if invoked when {@link #getStatus()} is {@code EMPTY} or {@code CLIENT}
     */
    public RockData getRockData()
    {
        return Objects.requireNonNull(rockData);
    }

    public void setRockData(RockData rockData)
    {
        this.rockData = rockData;
    }

    public int[] getAquiferSurfaceHeight()
    {
        return Objects.requireNonNull(aquiferSurfaceHeight, "Missing aquifer surface height at " + pos);
    }

    public void setAquiferSurfaceHeight(int[] aquiferSurfaceHeight)
    {
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
    }

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX(), pos.getZ());
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer == null ? UNKNOWN_RAINFALL : rainfallLayer.getValue((z & 15) / 16f, 1 - ((x & 15) / 16f));
    }

    public void setRainfall(LerpFloatLayer rainfallLayer)
    {
        this.rainfallLayer = rainfallLayer;
    }

    public float getAverageTemp(BlockPos pos)
    {
        return getAverageTemp(pos.getX(), pos.getZ());
    }

    public float getAverageTemp(int x, int z)
    {
        return temperatureLayer == null ? UNKNOWN_TEMPERATURE : temperatureLayer.getValue((z & 15) / 16f, 1 - ((x & 15) / 16f));
    }

    public void setAverageTemp(LerpFloatLayer temperatureLayer)
    {
        this.temperatureLayer = temperatureLayer;
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
    public void onUpdatePacket(@Nullable LerpFloatLayer rainfallLayer, @Nullable LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
        this.plateTectonicsInfo = plateTectonicsInfo;

        switch (status)
        {
            case EMPTY -> this.status = Status.CLIENT;
            case FULL -> throw new IllegalStateException("ChunkData#onUpdatePacket was called on full data: " + this);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ChunkDataCapability.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putByte("status", (byte) status.ordinal());
        if (status == Status.FULL)
        {
            nbt.putByte("plateTectonicsInfo", (byte) plateTectonicsInfo.ordinal());
            if (rainfallLayer != null)
            {
                nbt.put("rainfall", rainfallLayer.write());
            }
            if (temperatureLayer != null)
            {
                nbt.put("temperature", temperatureLayer.write());
            }
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
            if (rockData != null)
            {
                nbt.put("rockData", rockData.write(rockLayerSettings));
            }
            if (aquiferSurfaceHeight != null)
            {
                nbt.putIntArray("aquiferSurfaceHeight", aquiferSurfaceHeight);
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        status = Status.valueOf(nbt.getByte("status"));
        if (status == Status.FULL)
        {
            plateTectonicsInfo = PlateTectonicsClassification.valueOf(nbt.getByte("plateTectonicsInfo"));
            rainfallLayer = nbt.contains("rainfall") ? new LerpFloatLayer(nbt.getCompound("rainfall")) : null;
            temperatureLayer = nbt.contains("temperature") ? new LerpFloatLayer(nbt.getCompound("temperature")) : null;
            rockData = nbt.contains("rockData", Tag.TAG_COMPOUND) ? new RockData(nbt.getCompound("rockData"), rockLayerSettings) : null;
            aquiferSurfaceHeight = nbt.contains("aquiferSurfaceHeight") ? nbt.getIntArray("aquiferSurfaceHeight") : null;
            forestType = ForestType.valueOf(nbt.getByte("forestType"));
            forestWeirdness = nbt.getFloat("forestWeirdness");
            forestDensity = nbt.getFloat("forestDensity");
        }
        else
        {
            plateTectonicsInfo = PlateTectonicsClassification.OCEANIC;
            rainfallLayer = null;
            temperatureLayer = null;
            rockData = null;
            aquiferSurfaceHeight = null;
            forestType = ForestType.NONE;
            forestWeirdness = 0.5f;
            forestDensity = 0.5f;
        }
    }

    @Override
    public String toString()
    {
        return "ChunkData{pos=" + pos + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
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
            super(new ChunkPos(ChunkPos.INVALID_CHUNK_POS), RockLayerSettings.EMPTY);
        }

        @Override
        public void setRockData(RockData rockData)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setAquiferSurfaceHeight(int[] aquiferSurfaceHeight)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setRainfall(LerpFloatLayer rainfallLayer)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setAverageTemp(LerpFloatLayer temperatureLayer)
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
        public void onUpdatePacket(@Nullable LerpFloatLayer rainfallLayer, @Nullable LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
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