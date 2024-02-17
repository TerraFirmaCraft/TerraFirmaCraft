/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;

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

    public ChunkWatchPacket(int chunkX, int chunkZ, LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
    }

    ChunkWatchPacket(FriendlyByteBuf buffer)
    {
        chunkX = buffer.readVarInt();
        chunkZ = buffer.readVarInt();
        rainfallLayer = new LerpFloatLayer(buffer);
        temperatureLayer = new LerpFloatLayer(buffer);
        forestType = ForestType.valueOf(buffer.readByte());
        forestDensity = buffer.readFloat();
        forestWeirdness = buffer.readFloat();
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