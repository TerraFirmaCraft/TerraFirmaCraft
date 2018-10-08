/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.objects.container.ContainerKnapping;

public class PacketKnappingUpdate implements IMessage
{
    private int slotIdx;

    @SuppressWarnings("unused")
    public PacketKnappingUpdate()
    {
        // No-args constructor is required by forge
    }

    public PacketKnappingUpdate(int slotIdx)
    {
        this.slotIdx = slotIdx;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slotIdx = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(slotIdx);
    }

    public static class Handler implements IMessageHandler<PacketKnappingUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketKnappingUpdate message, MessageContext ctx)
        {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            if (serverPlayer.openContainer instanceof ContainerKnapping)
            {
                ContainerKnapping container = (ContainerKnapping) serverPlayer.openContainer;
                serverPlayer.getServerWorld().addScheduledTask(() ->
                    container.onUpdate(message.slotIdx)
                );
            }
            // No reply message
            return null;
        }
    }
}
