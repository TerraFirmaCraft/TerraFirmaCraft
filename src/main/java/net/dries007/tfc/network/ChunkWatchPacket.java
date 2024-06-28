/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Sent from server -> client on chunk watch, partially syncs chunk data and updates the client cache
 */
public record ChunkWatchPacket(
    int chunkX,
    int chunkZ,
    LerpFloatLayer rainfallLayer,
    LerpFloatLayer temperatureLayer,
    ForestType forestType,
    float forestWeirdness,
    float forestDensity
)
{
    ChunkWatchPacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readVarInt(),
            buffer.readVarInt(),
            new LerpFloatLayer(buffer),
            new LerpFloatLayer(buffer),
            ForestType.valueOf(buffer.readByte()),
            buffer.readFloat(),
            buffer.readFloat()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
        rainfallLayer.encode(buffer);
        temperatureLayer.encode(buffer);
        buffer.writeByte(forestType.ordinal());
        buffer.writeFloat(forestDensity);
        buffer.writeFloat(forestWeirdness);
    }

    void handle()
    {
        final Level level = ClientHelpers.getLevel();
        final ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        if (level != null)
        {
            ChunkData data = ChunkData.get(level, chunkPos);
            if (data.status() == ChunkData.Status.INVALID)
            {
                // The chunk has not been loaded yet on client, but we have data we need to get to the chunk,
                // so we store it to be populated later.
                data = ChunkData.queueClientChunkDataForLoad(chunkPos);
            }

            data.onUpdatePacket(rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness);
        }
    }
}