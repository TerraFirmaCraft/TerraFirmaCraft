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
import net.dries007.tfc.client.TFCGuiHandler;

public class PacketSwitchPlayerInventoryTab implements IMessage
{
    private TFCGuiHandler.Type typeToSwitchTo;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketSwitchPlayerInventoryTab() {}

    public PacketSwitchPlayerInventoryTab(TFCGuiHandler.Type typeToSwitchTo)
    {
        this.typeToSwitchTo = typeToSwitchTo;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        typeToSwitchTo = TFCGuiHandler.Type.valueOf(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(typeToSwitchTo.ordinal());
    }

    public static final class Handler implements IMessageHandler<PacketSwitchPlayerInventoryTab, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSwitchPlayerInventoryTab message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    player.openContainer.onContainerClosed(player);
                    if (message.typeToSwitchTo == TFCGuiHandler.Type.INVENTORY)
                    {
                        player.openContainer = player.inventoryContainer;
                    }
                    else
                    {
                        TFCGuiHandler.openGui(player.world, player, message.typeToSwitchTo);
                    }
                });
            }
            return null;
        }
    }
}
