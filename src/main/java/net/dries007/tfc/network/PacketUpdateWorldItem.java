/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TEWorldItem;

public class PacketUpdateWorldItem implements IMessage
{

    private BlockPos pos;
    private ItemStack stack;

    public PacketUpdateWorldItem(TEWorldItem te)
    {
        this(te.getPos(), te.inventory.getStackInSlot(0));
    }

    public PacketUpdateWorldItem(BlockPos pos, ItemStack stack)
    {
        this.pos = pos;
        this.stack = stack;
    }

    // Needed for forge to call this class
    public PacketUpdateWorldItem() {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<PacketUpdateWorldItem, IMessage>
    {

        @Override
        public IMessage onMessage(PacketUpdateWorldItem message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TEWorldItem te = (TEWorldItem) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if (te != null)
                {
                    TerraFirmaCraft.getLog().debug("Got the update packet: " + message.stack.getDisplayName());
                    te.inventory.setStackInSlot(0, message.stack);
                }
            });
            return null;
        }

    }
}
