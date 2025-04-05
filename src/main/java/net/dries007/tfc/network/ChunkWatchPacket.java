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
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;

/**
 * Sent from server -> client on chunk watch, partially syncs chunk data and updates the client cache
 */
public record ChunkWatchPacket(
    ChunkPos pos,
    LerpFloatLayer rainfall,
    LerpFloatLayer rainVariance,
    LerpFloatLayer baseGroundwater,
    LerpFloatLayer temperature,
    ForestType forestType,
    float accumulatedRainfall
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ChunkWatchPacket> TYPE = PacketHandler.type("chunk_watch");
    // Can't use composite here anymore, too many fields
    public static final StreamCodec<ByteBuf, ChunkWatchPacket> CODEC = new StreamCodec<>()
    {
        @Override
        public ChunkWatchPacket decode(ByteBuf byteBuf)
        {
            ChunkPos chunkPos = StreamCodecs.CHUNK_POS.decode(byteBuf);
            LerpFloatLayer rainfall = LerpFloatLayer.STREAM_CODEC.decode(byteBuf);
            LerpFloatLayer rainVariance = LerpFloatLayer.STREAM_CODEC.decode(byteBuf);
            LerpFloatLayer baseGroundwater = LerpFloatLayer.STREAM_CODEC.decode(byteBuf);
            LerpFloatLayer temperature = LerpFloatLayer.STREAM_CODEC.decode(byteBuf);
            ForestType forestType = ForestType.STREAM.decode(byteBuf);
            float accumulatedRainfall = ByteBufCodecs.FLOAT.decode(byteBuf);
            return new ChunkWatchPacket(chunkPos, rainfall, rainVariance, baseGroundwater, temperature, forestType, accumulatedRainfall);
        }

        @Override
        public void encode(ByteBuf o, ChunkWatchPacket chunkWatchPacket)
        {
            StreamCodecs.CHUNK_POS.encode(o, chunkWatchPacket.pos);
            LerpFloatLayer.STREAM_CODEC.encode(o, chunkWatchPacket.rainfall);
            LerpFloatLayer.STREAM_CODEC.encode(o, chunkWatchPacket.rainVariance);
            LerpFloatLayer.STREAM_CODEC.encode(o, chunkWatchPacket.baseGroundwater);
            LerpFloatLayer.STREAM_CODEC.encode(o, chunkWatchPacket.temperature);
            ForestType.STREAM.encode(o, chunkWatchPacket.forestType);
            ByteBufCodecs.FLOAT.encode(o, chunkWatchPacket.accumulatedRainfall);
        }
    };

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
                data.onUpdatePacket(rainfall, rainVariance, baseGroundwater, temperature, forestType, accumulatedRainfall);
            }
        }
    }
}