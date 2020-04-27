package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class ChunkDataUpdatePacket
{
    private final int chunkX;
    private final int chunkZ;
    private final float rainfall;
    private final float regionalTemp;

    public ChunkDataUpdatePacket(int chunkX, int chunkZ, ChunkData data)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rainfall = data.getRainfall();
        this.regionalTemp = data.getRegionalTemp();
    }

    ChunkDataUpdatePacket(PacketBuffer buffer)
    {
        chunkX = buffer.readInt();
        chunkZ = buffer.readInt();
        rainfall = buffer.readFloat();
        regionalTemp = buffer.readFloat();
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeFloat(rainfall);
        buffer.writeFloat(regionalTemp);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            // Update client-side chunk data capability
            World world = Minecraft.getInstance().world;
            if (world != null)
            {
                // Update climate cache
                IChunk chunk = world.getChunk(chunkX, chunkZ);
                ChunkData.get(chunk).ifPresent(data -> {
                    // Update client side chunk data
                    data.setValid(true);
                    data.setRegionalTemp(regionalTemp);
                    data.setRainfall(rainfall);

                    // Update climate cache
                    ClimateTFC.update(chunk.getPos(), data.getAvgTemp(), data.getRainfall());
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
