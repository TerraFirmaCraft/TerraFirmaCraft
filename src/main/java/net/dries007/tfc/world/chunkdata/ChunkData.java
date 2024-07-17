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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCAttachments;
import net.dries007.tfc.network.ChunkWatchPacket;


public class ChunkData
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    private static final float UNKNOWN_RAINFALL = 250;
    private static final float UNKNOWN_TEMPERATURE = 10;

    /**
     * Accesses the chunk data from a given level, at a given position. This method <strong>may deadlock</strong> if called on a {@link ServerLevel}
     * from within a world generation context, as it will try and load the chunk. Make sure you are accessing the correct level for the context provided.
     *
     * @see #get(ChunkAccess)
     */
    public static ChunkData get(LevelReader level, BlockPos pos)
    {
        return get(level.getChunk(pos));
    }

    /**
     * Accesses the chunk data from a given level, at a given position. This method <strong>may deadlock</strong> if called on a {@link ServerLevel}
     * from within a world generation context, as it will try and load the chunk. Make sure you are accessing the correct level for the context provided.
     *
     * @see #get(ChunkAccess)
     */
    public static ChunkData get(LevelReader level, ChunkPos pos)
    {
        return get(level.getChunk(pos.x, pos.z));
    }

    /**
     * Accesses the chunk data from the given chunk. This is safe to call at all points during world generation, on server, or on client if proper
     * access to a chunk is already made. It may return different things when called in different contexts:
     * <ul>
     *     <li><strong>On Client</strong>, this will return a client-side, shallow copy of the chunk data, which is synced on chunk watch and unwatch
     *     to individual players</li>
     *     <li><strong>On Server</strong>, when invoked with a {@link LevelChunk}, this will return the full view of the chunk data.</li>
     *     <li><strong>During World Generation</strong>, this will always return a view of the chunk data, generated as much as possible. If the chunk
     *     is an impostor, the view of the underlying chunk will be returned.</li>
     * </ul>
     * Note that this should only be used for mutable access when the caller has ensured that the chunk is mutable at the time - an impostor, or empty
     * chunk will not allow mutation, or the chunk data on client should not be mutated under any case!
     *
     * @see #get(LevelReader, BlockPos)
     * @see #get(LevelReader, ChunkPos)
     */
    public static ChunkData get(ChunkAccess chunk)
    {
        return chunk instanceof ImposterProtoChunk impostor ? get(impostor.getWrapped())
            : chunk instanceof EmptyLevelChunk ? ChunkData.EMPTY
            : chunk.getData(TFCAttachments.CHUNK_DATA);
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