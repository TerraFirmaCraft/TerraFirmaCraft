/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.ChunkWatchPacket;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class ChunkData implements ICapabilitySerializable<CompoundTag>
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();
    public static final Capability<ChunkData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "chunk_data");

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
            return chunk.getCapability(CAPABILITY);
        }
        return LazyOptional.empty();
    }

    private final LazyOptional<ChunkData> capability;
    @Nullable private final ChunkDataGenerator generator;
    private final ChunkPos pos;

    private Status status;

    private final RockData rockData;
    @Nullable private LerpFloatLayer rainfallLayer;
    @Nullable private LerpFloatLayer temperatureLayer;
    private int @Nullable [] aquiferSurfaceHeight;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;

    public ChunkData(ChunkPos pos)
    {
        this(null, pos);
    }

    public ChunkData(@Nullable ChunkDataGenerator generator, ChunkPos pos)
    {
        this.generator = generator;
        this.pos = pos;
        this.capability = LazyOptional.of(() -> this);
        this.status = Status.EMPTY;
        this.rockData = new RockData(generator);
        this.forestType = ForestType.NONE;
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    /**
     * Note: this method will throw if invoked when {@link #status()} is {@code EMPTY} or {@code CLIENT}
     */
    public RockData getRockData()
    {
        return rockData;
    }

    public int[] getAquiferSurfaceHeight()
    {
        assert aquiferSurfaceHeight != null;
        return aquiferSurfaceHeight;
    }

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX(), pos.getZ());
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer == null ? UNKNOWN_RAINFALL : rainfallLayer.getValue((x & 15) / 16f, (z & 15) / 16f);
    }

    public float getAverageTemp(BlockPos pos)
    {
        return getAverageTemp(pos.getX(), pos.getZ());
    }

    public float getAverageTemp(int x, int z)
    {
        return temperatureLayer == null ? UNKNOWN_TEMPERATURE : temperatureLayer.getValue((z & 15) / 16f, (x & 15) / 16f);
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

    public Status status()
    {
        return status;
    }

    /**
     * Generate the chunk data from empty to {@link Status#PARTIAL}. Populated lazily on first creation, and guaranteed to be done by structure stage.
     */
    public void generatePartial(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestWeirdness, float forestDensity)
    {
        assert status == Status.EMPTY;

        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestWeirdness = forestWeirdness;
        this.forestDensity = forestDensity;
        this.status = Status.PARTIAL;
    }

    /**
     * Generate the chunk data from {@link Status#PARTIAL} to {@link Status#FULL}. Generated during fill noise stage once this data is prepared.
     */
    public void generateFull(int[] surfaceHeight, int[] aquiferSurfaceHeight)
    {
        assert status == Status.PARTIAL;

        this.rockData.setSurfaceHeight(surfaceHeight);
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
        this.status = Status.FULL;
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
    public void onUpdatePacket(@Nullable LerpFloatLayer rainfallLayer, @Nullable LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;

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
        return CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putByte("status", (byte) status.ordinal());
        if (status == Status.FULL)
        {
            assert aquiferSurfaceHeight != null;

            nbt.putIntArray("surfaceHeight", rockData.getSurfaceHeight());
            nbt.putIntArray("aquiferSurfaceHeight", aquiferSurfaceHeight);
        }
        if (status == Status.FULL || status == Status.PARTIAL)
        {
            assert rainfallLayer != null;
            assert temperatureLayer != null;

            nbt.put("rainfall", rainfallLayer.write());
            nbt.put("temperature", temperatureLayer.write());
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        status = Status.valueOf(nbt.getByte("status"));
        if (status == Status.FULL)
        {
            assert generator != null;

            rockData.setSurfaceHeight(nbt.getIntArray("surfaceHeight"));
            aquiferSurfaceHeight = nbt.getIntArray("aquiferSurfaceHeight");
        }
        if (status == Status.FULL || status == Status.PARTIAL)
        {
            rainfallLayer = new LerpFloatLayer(nbt.getCompound("rainfall"));
            temperatureLayer = new LerpFloatLayer(nbt.getCompound("temperature"));
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

    public enum Status
    {
        EMPTY, // Default, un-generated chunk data
        CLIENT, // Client-side shallow copy
        PARTIAL, // Partially generated (before fill noise)
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
        public void generatePartial(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestWeirdness, float forestDensity) { error(); }

        @Override
        public void generateFull(int[] surfaceHeight, int[] aquiferSurfaceHeight) { error(); }

        @Override
        public void onUpdatePacket(@Nullable LerpFloatLayer rainfallLayer, @Nullable LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness) { error(); }

        @Override
        public void deserializeNBT(CompoundTag nbt) { error(); }

        @Override
        public String toString()
        {
            return "ImmutableChunkData";
        }

        private void error()
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }
    }
}