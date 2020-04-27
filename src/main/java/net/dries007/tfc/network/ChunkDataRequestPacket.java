package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.world.chunkdata.ChunkData;

public class ChunkDataRequestPacket
{
    private final int chunkX;
    private final int chunkZ;

    public ChunkDataRequestPacket(int chunkX, int chunkZ)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public ChunkDataRequestPacket(PacketBuffer buffer)
    {
        chunkX = buffer.readInt();
        chunkZ = buffer.readInt();
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        // Tell the server to send back the chunk data
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null)
            {
                World world = player.getServerWorld();
                ChunkData.get(world.getChunk(chunkX, chunkZ)).ifPresent(data -> {
                    PacketHandler.get().send(PacketDistributor.PLAYER.with(context.get()::getSender), new ChunkDataUpdatePacket(chunkX, chunkZ, data));
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
