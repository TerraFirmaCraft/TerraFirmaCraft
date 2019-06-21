/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.objects.te.TEQuern;
import net.dries007.tfc.util.Helpers;

public class PacketRequestQuernUpdate implements IMessage
{

    private BlockPos pos;
    private int dimension;

    public PacketRequestQuernUpdate(BlockPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestQuernUpdate(TEQuern te)
    {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    public PacketRequestQuernUpdate()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    public static class Handler implements IMessageHandler<PacketRequestQuernUpdate, PacketQuernUpdate>
    {
        @Override
        public PacketQuernUpdate onMessage(PacketRequestQuernUpdate message, MessageContext ctx)
        {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            TEQuern te = Helpers.getTE(world, message.pos, TEQuern.class);
            if (te != null)
            {
                return new PacketQuernUpdate(te);
            }
            else
            {
                return null;
            }
        }
    }
}
