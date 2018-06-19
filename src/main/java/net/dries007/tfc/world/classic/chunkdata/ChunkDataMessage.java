/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

public class ChunkDataMessage implements IMessage
{
    private NBTTagCompound data;
    private int x, z;

    @SuppressWarnings("unused")
    public ChunkDataMessage()
    {

    }

    public ChunkDataMessage(ChunkPos chunk, NBTTagCompound data)
    {
        x = chunk.x;
        z = chunk.z;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        z = buf.readInt();
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(z);
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<ChunkDataMessage, IMessage>
    {
        @Override
        public IMessage onMessage(ChunkDataMessage message, MessageContext ctx)
        {
            if (ctx.side.isClient())
            {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Chunk c = Minecraft.getMinecraft().world.getChunkFromChunkCoords(message.x, message.z);
                    ChunkDataTFC data = c.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
                    if (data != null)
                        ChunkDataProvider.CHUNK_DATA_CAPABILITY.readNBT(data, null, message.data);
                });
            }
            return null;
        }
    }
}
