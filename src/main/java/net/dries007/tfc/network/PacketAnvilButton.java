/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.container.ContainerAnvilTFC;

public class PacketAnvilButton implements IMessage
{
    private int buttonId;

    // no args constructor required for forge
    @SuppressWarnings("unused")
    public PacketAnvilButton() {}

    public PacketAnvilButton(int buttonId)
    {
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        buttonId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(buttonId);
    }

    public static class Handler implements IMessageHandler<PacketAnvilButton, IMessage>
    {
        @Override
        public IMessage onMessage(PacketAnvilButton message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player.openContainer instanceof ContainerAnvilTFC)
            {
                ContainerAnvilTFC container = (ContainerAnvilTFC) player.openContainer;
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() ->
                    container.onReceivePacket(message.buttonId)
                );
            }
            return null;
        }
    }
}
