/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;

public class PacketOpenCraftingGui implements IMessageEmpty
{
    public static final class Handler implements IMessageHandler<PacketOpenCraftingGui, IMessage>
    {
        @Override
        public IMessage onMessage(PacketOpenCraftingGui message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    TFCGuiHandler.openGui(player.world, player, TFCGuiHandler.Type.CRAFTING);
                }
            });
            return null;
        }
    }
}
