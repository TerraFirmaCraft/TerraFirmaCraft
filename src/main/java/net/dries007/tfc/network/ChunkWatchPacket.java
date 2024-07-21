/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.TFCAttachments;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;

/**
 * Sent from server -> client on chunk watch, partially syncs chunk data and updates the client cache
 */
public record ChunkWatchPacket(
    ChunkPos pos,
    LerpFloatLayer rainfall,
    LerpFloatLayer temperature,
    ForestType forestType,
    float forestWeirdness,
    float forestDensity
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ChunkWatchPacket> TYPE = PacketHandler.type("chunk_watch");
    public static final StreamCodec<ByteBuf, ChunkWatchPacket> CODEC = StreamCodec.composite(
        StreamCodecs.CHUNK_POS, c -> c.pos,
        LerpFloatLayer.STREAM, c -> c.rainfall,
        LerpFloatLayer.STREAM, c -> c.temperature,
        ForestType.STREAM, c -> c.forestType,
        ByteBufCodecs.FLOAT, c -> c.forestWeirdness,
        ByteBufCodecs.FLOAT, c -> c.forestDensity,
        ChunkWatchPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final LevelChunk chunk = level.getChunk(pos.x, pos.z);
            ChunkData data = ChunkData.get(chunk);
            if (data.status() == ChunkData.Status.INVALID)
            {
                // The chunk has not been loaded yet on client, but we have data we need to get to the chunk,
                // so we store it to be populated later.
                data = ChunkData.queueClientChunkDataForLoad(pos);
            }

            data.onUpdatePacket(rainfall, temperature, forestType, forestDensity, forestWeirdness);
            chunk.setData(TFCAttachments.CHUNK_DATA.get(), data);
        }
    }
}