/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.world.chunkdata.*;

/**
 * Sent from server -> client on chunk watch, partially syncs chunk data and updates the client cache
 */
public class ChunkWatchPacket
{
    private final int chunkX;
    private final int chunkZ;
    private final LerpFloatLayer rainfallLayer;
    private final LerpFloatLayer temperatureLayer;
    private final ForestType forestType;
    private final float forestWeirdness;
    private final float forestDensity;
    private final PlateTectonicsClassification plateTectonicsInfo;

    public ChunkWatchPacket(int chunkX, int chunkZ, LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
        this.plateTectonicsInfo = plateTectonicsInfo;
    }

    ChunkWatchPacket(PacketBuffer buffer)
    {
        chunkX = buffer.readVarInt();
        chunkZ = buffer.readVarInt();
        rainfallLayer = new LerpFloatLayer(buffer);
        temperatureLayer = new LerpFloatLayer(buffer);
        forestType = ForestType.valueOf(buffer.readByte());
        forestDensity = buffer.readFloat();
        forestWeirdness = buffer.readFloat();
        plateTectonicsInfo = PlateTectonicsClassification.valueOf(buffer.readByte());
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
        rainfallLayer.serialize(buffer);
        temperatureLayer.serialize(buffer);
        buffer.writeByte(forestType.ordinal());
        buffer.writeFloat(forestDensity);
        buffer.writeFloat(forestWeirdness);
        buffer.writeByte(plateTectonicsInfo.ordinal());
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            // Update client-side chunk data capability
            World world = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientHelpers::getWorld);
            if (world != null)
            {
                // First, synchronize the chunk data in the capability and cache.
                // Then, update the single data instance with the packet data
                IChunk chunk = world.hasChunk(chunkX, chunkZ) ? world.getChunk(chunkX, chunkZ) : null;
                ChunkData data = ChunkData.getCapability(chunk)
                    .map(dataIn -> {
                        ChunkDataCache.CLIENT.update(pos, dataIn);
                        return dataIn;
                    }).orElseGet(() -> ChunkDataCache.CLIENT.getOrCreate(pos));
                data.onUpdatePacket(rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness, plateTectonicsInfo);
            }
        });
        context.get().setPacketHandled(true);
    }
}