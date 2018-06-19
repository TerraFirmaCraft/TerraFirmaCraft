/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TEWorldItem;

public class PacketRequestWorldItem implements IMessage
{

    private BlockPos pos;
    private int dimension;

    public PacketRequestWorldItem(BlockPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestWorldItem(TEWorldItem te)
    {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    // Needs to be here for forge
    public PacketRequestWorldItem() { }

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

    public static class Handler implements IMessageHandler<PacketRequestWorldItem, PacketUpdateWorldItem>
    {

        @Override
        public PacketUpdateWorldItem onMessage(PacketRequestWorldItem message, MessageContext ctx)
        {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            if (!world.isBlockLoaded(message.pos)) return null;
            TEWorldItem te = (TEWorldItem) world.getTileEntity(message.pos);
            if (te != null)
            {
                TerraFirmaCraft.getLog().debug("Sent an update packet " + te.inventory.getStackInSlot(0).getDisplayName());
                return new PacketUpdateWorldItem(te);
            }
            else
            {
                return null;
            }
        }

    }
}
