/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.world.chunkdata.ChunkDataCache;

/**
 * Sent from server -> client, clears the client side chunk data cache when a chunk is unwatched
 */
public class ChunkUnwatchPacket
{
    private final int chunkX;
    private final int chunkZ;

    public ChunkUnwatchPacket(ChunkPos pos)
    {
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
    }

    public ChunkUnwatchPacket(PacketBuffer buffer)
    {
        this.chunkX = buffer.readVarInt();
        this.chunkZ = buffer.readVarInt();
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ChunkDataCache.CLIENT.remove(new ChunkPos(chunkX, chunkZ)));
        context.get().setPacketHandled(true);
    }
}