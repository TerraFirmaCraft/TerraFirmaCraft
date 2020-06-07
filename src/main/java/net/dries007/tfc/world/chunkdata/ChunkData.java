/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Optional;
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
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.network.ChunkDataRequestPacket;
import net.dries007.tfc.network.ChunkDataUpdatePacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.LerpFloatLayer;

public class ChunkData implements ICapabilitySerializable<CompoundNBT>
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * @see ChunkData#get(IWorld, ChunkPos, Status, boolean)
     */
    public static ChunkData get(IWorld world, BlockPos pos, Status requiredStatus, boolean loadChunk)
    {
        return get(world, new ChunkPos(pos), requiredStatus, loadChunk);
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request packets if necessary
     * If on server, will either query capability falling back to cache, or query provider to generate the data.
     *
     * @see ChunkDataProvider#get(ChunkPos, Status) to directly force chunk generation, or if a world is not available
     * @see ChunkDataCache#get(ChunkPos) to directly access the cache
     */
    public static ChunkData get(IWorld world, ChunkPos pos, Status requiredStatus, boolean loadChunk)
    {
        if (world.isRemote())
        {
            if (requiredStatus.ordinal() > Status.CLIMATE.ordinal())
            {
                LOGGER.warn("Illegal request of chunk data with status {} on client", requiredStatus);
                LOGGER.debug(new IllegalStateException("Stacktrace"));
            }
            // Client side, with world context
            // First query the capability, fallback to cache with network request if cache miss
            IChunk chunkIn = world.getChunk(pos.asBlockPos());
            return Optional.ofNullable(chunkIn instanceof Chunk ? (Chunk) chunkIn : null)
                .map(chunk -> chunk.getCapability(ChunkDataCapability.CAPABILITY))
                .orElseGet(LazyOptional::empty)
                .orElseGet(() -> {
                    ChunkData cached = ChunkDataCache.get(pos);
                    if (!cached.getStatus().isAtLeast(Status.CLIMATE))
                    {
                        PacketHandler.send(PacketDistributor.SERVER.noArg(), new ChunkDataRequestPacket(pos.x, pos.z));
                    }
                    return cached;
                });
        }
        else if (loadChunk)
        {
            // Server side, with world context, allowed to force chunk load
            // First query capability, fallback to cache
            IChunk chunkIn = world.chunkExists(pos.x, pos.z) ? world.getChunk(pos.asBlockPos()) : null;
            return Optional.ofNullable(chunkIn instanceof Chunk ? (Chunk) chunkIn : null)
                .map(chunk -> chunk.getCapability(ChunkDataCapability.CAPABILITY))
                .orElseGet(LazyOptional::empty)
                .orElseGet(() -> ChunkDataCache.get(pos));
        }
        else
        {
            // Server side, with world context, not allowed for force chunk load
            // This call path is during world generation, so we generate the data up to the required status
            return ChunkDataProvider.get(world)
                .map(provider -> provider.get(pos, requiredStatus))
                .orElseThrow(() -> new IllegalStateException("No ChunkDataProvider present on logical server?"));
        }
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static LazyOptional<ChunkData> get(@Nullable IChunk chunk)
    {
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY);
        }
        return LazyOptional.empty();
    }

    private final LazyOptional<ChunkData> capability = LazyOptional.of(() -> this);

    private RockData rockData;
    private Status status;
    private LerpFloatLayer rainfallLayer;
    private LerpFloatLayer temperatureLayer;

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

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX() & 15, pos.getZ() & 15);
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer.getValue(x / 16f, z / 16f);
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
        return temperatureLayer.getValue(x / 16f, z / 16f);
    }

    public void setAverageTemp(float tempNW, float tempNE, float tempSW, float tempSE)
    {
        temperatureLayer.init(tempNW, tempNE, tempSW, tempSE);
    }

    public Status getStatus()
    {
        return status;
    }

    /**
     * Create an update packet to send to client with necessary information
     */
    public ChunkDataUpdatePacket getUpdatePacket(int chunkX, int chunkZ)
    {
        return new ChunkDataUpdatePacket(chunkX, chunkZ, rainfallLayer, temperatureLayer);
    }

    /**
     * Called on client, sets to received data
     */
    public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.status = Status.CLIMATE;
    }

    /**
     * Called on client, copies from newer data
     */
    public void copyFrom(ChunkData other)
    {
        this.rockData = other.rockData;
        this.rainfallLayer = other.rainfallLayer;
        this.temperatureLayer = other.temperatureLayer;
        this.status = other.status;
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
            nbt.put("rainfall", rainfallLayer.serializeNBT());
            nbt.put("temperature", temperatureLayer.serializeNBT());
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
                rainfallLayer.deserializeNBT(nbt.getCompound("rainfall"));
                temperatureLayer.deserializeNBT(nbt.getCompound("temperature"));
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
        rainfallLayer = new LerpFloatLayer(250);
        temperatureLayer = new LerpFloatLayer(10);
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
