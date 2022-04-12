/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.*;

/**
 * Sent from server -> client on chunk watch, partially syncs chunk data and updates the client cache
 */
public class ChunkWatchPacket
{
    private final int chunkX;
    private final int chunkZ;
    @Nullable private final LerpFloatLayer rainfallLayer;
    @Nullable private final LerpFloatLayer temperatureLayer;
    private final ForestType forestType;
    private final float forestWeirdness;
    private final float forestDensity;
    private final PlateTectonicsClassification plateTectonicsInfo;

    public ChunkWatchPacket(int chunkX, int chunkZ, @Nullable LerpFloatLayer rainfallLayer, @Nullable LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
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

    ChunkWatchPacket(FriendlyByteBuf buffer)
    {
        chunkX = buffer.readVarInt();
        chunkZ = buffer.readVarInt();
        rainfallLayer = Helpers.decodeNullable(buffer, LerpFloatLayer::new);
        temperatureLayer = Helpers.decodeNullable(buffer, LerpFloatLayer::new);
        forestType = ForestType.valueOf(buffer.readByte());
        forestDensity = buffer.readFloat();
        forestWeirdness = buffer.readFloat();
        plateTectonicsInfo = PlateTectonicsClassification.valueOf(buffer.readByte());
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
        Helpers.encodeNullable(rainfallLayer, buffer, LerpFloatLayer::encode);
        Helpers.encodeNullable(temperatureLayer, buffer, LerpFloatLayer::encode);
        buffer.writeByte(forestType.ordinal());
        buffer.writeFloat(forestDensity);
        buffer.writeFloat(forestWeirdness);
        buffer.writeByte(plateTectonicsInfo.ordinal());
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            // Update client-side chunk data capability
            Level world = ClientHelpers.getLevel();
            if (world != null)
            {
                // First, synchronize the chunk data in the capability and cache.
                // Then, update the single data instance with the packet data
                ChunkAccess chunk = world.hasChunk(chunkX, chunkZ) ? world.getChunk(chunkX, chunkZ) : null;
                ChunkData data = ChunkData.getCapability(chunk)
                    .map(dataIn -> {
                        ChunkDataCache.CLIENT.update(pos, dataIn);
                        return dataIn;
                    }).orElseGet(() -> ChunkDataCache.CLIENT.computeIfAbsent(pos, ChunkData::createClient));
                data.onUpdatePacket(rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness, plateTectonicsInfo);
            }
        });
    }
}