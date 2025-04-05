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
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Sent from server -> client on chunk rain update, updates the client cache
 */
public record ChunkRainfallPacket(
    ChunkPos pos,
    float accumulatedRainfall
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ChunkRainfallPacket> TYPE = PacketHandler.type("chunk_rainfall");
    public static final StreamCodec<ByteBuf, ChunkRainfallPacket> CODEC = StreamCodec.composite(
        StreamCodecs.CHUNK_POS, ChunkRainfallPacket::pos,
        ByteBufCodecs.FLOAT, ChunkRainfallPacket::accumulatedRainfall,
        ChunkRainfallPacket::new
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
            final ChunkData data = ChunkData.get(chunk);
            if (data.status() != ChunkData.Status.INVALID)
            {
                data.setAccumulatedRainfall(chunk, accumulatedRainfall);
            }
        }
    }
}
