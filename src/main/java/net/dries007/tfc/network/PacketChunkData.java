/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.ClimateRenderHelper;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class PacketChunkData implements IMessage
{
    private NBTTagCompound nbt;
    private int x, z;
    private float temperature, rainfall;

    @SuppressWarnings("unused")
    public PacketChunkData()
    {

    }

    public PacketChunkData(ChunkPos chunkPos, NBTTagCompound nbt, float temperature, float rainfall)
    {
        this.x = chunkPos.x;
        this.z = chunkPos.z;
        this.nbt = nbt;
        this.temperature = temperature;
        this.rainfall = rainfall;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        z = buf.readInt();
        nbt = ByteBufUtils.readTag(buf);
        temperature = buf.readFloat();
        rainfall = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(z);
        ByteBufUtils.writeTag(buf, nbt);
        buf.writeFloat(temperature);
        buf.writeFloat(rainfall);
    }

    public static class Handler implements IMessageHandler<PacketChunkData, IMessage>
    {
        @Override
        public IMessage onMessage(PacketChunkData message, MessageContext ctx)
        {
            final World world = TerraFirmaCraft.getProxy().getWorld(ctx);
            if (world != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    // Update client-side chunk data capability
                    Chunk chunk = world.getChunk(message.x, message.z);
                    ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
                    if (data != null)
                    {
                        ChunkDataProvider.CHUNK_DATA_CAPABILITY.readNBT(data, null, message.nbt);
                    }

                    // Update rendering climate helper
                    ClimateRenderHelper.update(chunk.getPos(), message.temperature, message.rainfall);
                });
            }
            return null;
        }
    }
}
