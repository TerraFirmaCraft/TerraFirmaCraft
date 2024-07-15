/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.world.ChunkGeneratorExtension;

/**
 * Additional data which is attached to chunks during world generation and used by various phase of the TFC chunk generator,
 * and subsequent features. Most of this data is persisted after world generation attached to the {@link LevelChunk}. A shallow
 * copy is synced to the client.
 * <p>
 * In order to query chunk data during world generation, <strong>always</strong> go through {@link ChunkDataProvider}, which can be
 * accessed through {@link ChunkGeneratorExtension} - either accessed directly, i.e. in feature generation, or through the level.
 */
public class ChunkData
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    private static final float UNKNOWN_RAINFALL = 250;
    private static final float UNKNOWN_TEMPERATURE = 10;

    /**
     * Returns the chunk data present at this {@code level} and {@code pos}. This cannot be used during world generation, and on client will return a {@link Status#CLIENT} chunk data.
     * @return the chunk data, or {@link #EMPTY} if the chunk is not loaded, or data is not available (yet).
     */
    public static ChunkData get(LevelReader level, BlockPos pos)
    {
        return get(level, new ChunkPos(pos));
    }

    /**
     * Returns the chunk data present at this {@code level} and {@code pos}. This cannot be used during world generation, and on client will return a {@link Status#CLIENT} chunk data.
     * @return the chunk data, or {@link #EMPTY} if the chunk is not loaded, or data is not available (yet).
     */
    @SuppressWarnings("deprecation")
    public static ChunkData get(LevelReader level, ChunkPos pos)
    {
        return level.hasChunk(pos.x, pos.z)
            && level.getChunk(pos.x, pos.z) instanceof LevelChunk levelChunk
                ? get(levelChunk)
                : EMPTY;
    }

    /**
     * Returns the chunk data present at this {@code chunk}. This cannot be used during world generation, and on client will return a {@link Status#CLIENT} chunk data.
     * @return the chunk data, or {@link #EMPTY} if the chunk is not loaded, or data is not available (yet).
     */
    public static ChunkData get(LevelChunk chunk)
    {
        return EMPTY; // todo 1.21, chunk data needs to work
    }

    public static void update(LevelChunk chunk, ChunkData data)
    {
        // todo: 1.21, chunk data needs to work
    }

    private static final Map<ChunkPos, ChunkData> CLIENT_CHUNK_QUEUE = new Object2ObjectOpenHashMap<>(128);

    public static ChunkData queueClientChunkDataForLoad(ChunkPos pos)
    {
        final ChunkData data = new ChunkData(pos);
        CLIENT_CHUNK_QUEUE.put(pos, data);
        return data;
    }

    public static ChunkData dequeueClientChunkData(ChunkPos pos)
    {
        final @Nullable ChunkData data = CLIENT_CHUNK_QUEUE.remove(pos);
        return data == null ? new ChunkData(pos) : data;
    }

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
        this.status = Status.EMPTY;
        this.rockData = new RockData(generator);
        this.forestType = ForestType.NONE;
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    /**
     * Returns the {@link RockData} for this chunk data. This is only valid on logical server.
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
        return temperatureLayer == null ? UNKNOWN_TEMPERATURE : temperatureLayer.getValue((x & 15) / 16f, (z & 15) / 16f);
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
        assert status == Status.FULL;
        assert rainfallLayer != null && temperatureLayer != null;

        // todo: chunk data syncing needs to work
        return null; // return new ChunkWatchPacket(pos.x, pos.z, rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness);
    }

    /**
     * Called on client, sets to received data
     */
    public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness)
    {
        assert status == Status.EMPTY || status == Status.CLIENT;

        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
        this.status = Status.CLIENT;
    }

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
        FULL, // Fully generated chunk data
        INVALID; // Invalid chunk data

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
        public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness) { error(); }

        @Override
        public void deserializeNBT(CompoundTag nbt) { error(); }

        @Override
        public Status status()
        {
            return Status.INVALID;
        }

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